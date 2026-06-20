import { Injectable, signal } from '@angular/core';
import { McpClientService } from './mcp-client.service';

export interface ChatMessage {
  role: 'user' | 'assistant' | 'system' | 'tool';
  content: string;
  toolCall?: { name: string; args: string; result: string };
}

@Injectable({ providedIn: 'root' })
export class AgentService {
  messages = signal<ChatMessage[]>([]);
  processing = signal(false);

  private readonly systemPrompt = `You are a helpful B2B auto parts assistant. You help customers check order statuses and create new orders.

Available tools:
- check_order_status(orderId: string): Check the status of an existing order
- create_b2b_order(accountId: string, items: [{sku: string, quantity: number}]): Create a new order

When a user asks about an order, extract the order ID and call check_order_status.
When a user wants to place an order, extract the account ID and items, then call create_b2b_order.
Be concise and professional.`;

  constructor(private mcp: McpClientService) {}

  async sendMessage(userText: string): Promise<void> {
    this.messages.update(m => [...m, { role: 'user', content: userText }]);
    this.processing.set(true);

    try {
      const intent = this.classifyIntent(userText);
      let response: string;

      if (intent === 'check_order') {
        const orderId = this.extractOrderId(userText);
        if (!orderId) {
          response = 'I couldn\'t find an order number. Please include it in your message.\n\nExample: "Check ORD-001" or just type "ORD-001"';
        } else {
          const toolResult = await this.mcp.callTool('check_order_status', { orderId }) as { content: { type: string; text: string }[] };
          const parsed = JSON.parse(toolResult.content[0].text);
          response = [
            `Order ${parsed.orderId}`,
            '',
            `Status: ${parsed.status}`,
            `Account: ${parsed.accountName}`,
            `Total: $${parsed.totalAmount.toFixed(2)}`,
            `Date: ${parsed.orderDate?.substring(0, 10)}`,
            '',
            'Items:',
            ...parsed.items.map((i: { sku: string; partName: string; quantity: number; unitPrice: number }) =>
              `  - ${i.partName} (${i.sku}) x${i.quantity} @ $${i.unitPrice.toFixed(2)}`
            ),
          ].join('\n');
        }
      } else if (intent === 'create_order') {
        const parsed = this.parseOrderRequest(userText);
        if (!parsed) {
          response = 'I need an account number and items to create an order.\n\nExample: "Create order for ACC-001 with 10 BRK-001 and 20 FLT-001"\n\nAvailable accounts: ACC-001, ACC-002, ACC-003';
        } else {
          const toolResult = await this.mcp.callTool('create_b2b_order', {
            accountId: parsed.accountId,
            items: parsed.items,
          }) as { content: { type: string; text: string }[] };
          const data = JSON.parse(toolResult.content[0].text);
          response = [
            'Order created successfully!',
            '',
            `Order: ${data.orderId}`,
            `Total: $${data.totalAmount.toFixed(2)}`,
            `Status: ${data.status}`,
            '',
            'Items ordered:',
            ...data.items.map((i: { sku: string; partName: string; quantity: number }) =>
              `  - ${i.partName} (${i.sku}) x${i.quantity}`
            ),
          ].join('\n');
        }
      } else {
        response = this.generateFallbackResponse(userText);
      }

      this.messages.update(m => [...m, { role: 'assistant', content: response }]);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'An error occurred';
      this.messages.update(m => [...m, { role: 'assistant', content: `❌ Error: ${msg}` }]);
    } finally {
      this.processing.set(false);
    }
  }

  private classifyIntent(text: string): 'check_order' | 'create_order' | 'general' {
    if (this.extractOrderId(text)) return 'check_order';

    const lower = text.toLowerCase();
    const checkWords = /\bcheck\b|\bstatus\b|\bwhere\s+is\b|\btrack\b|\btrack\b|\blookup\b|\bfind\b/;
    const createWords = /\bbook\b|\bbuy\b|\bpurchase\b|\bcreate\b|\bplace\b|\bnew\b|\badd\b|\border\b|\bneed\b|\bwant\b/;
    const matchesCheck = checkWords.test(lower);
    const matchesCreate = createWords.test(lower);

    if (matchesCheck && !matchesCreate) return 'check_order';
    if (matchesCreate && !matchesCheck) return 'create_order';
    if (matchesCheck && matchesCreate) return 'check_order';
    return 'general';
  }

  private extractOrderId(text: string): string | null {
    const match = text.match(/ORD[-_][A-Z0-9]+/i);
    return match ? match[0].toUpperCase() : null;
  }

  private parseOrderRequest(text: string): { accountId: string; items: { sku: string; quantity: number }[] } | null {
    const accountMatch = text.match(/ACC[-_][0-9]{3,}/i);
    if (!accountMatch) return null;
    const accountId = accountMatch[0].toUpperCase();
    const skuMatches = text.matchAll(/\b([A-Z]{3,4}[-_][0-9]{3,})\b/gi);
    const items: { sku: string; quantity: number }[] = [];
    for (const m of skuMatches) {
      if (m[0].startsWith('ACC-')) continue;
      const qtyMatch = text.match(new RegExp(`(\\d+)\\s*(of\\s*)?${m[0].replace('-', '[-_]?')}`, 'i'));
      const quantity = qtyMatch ? parseInt(qtyMatch[1]) : 1;
      items.push({ sku: m[0].toUpperCase(), quantity });
    }
    return items.length > 0 ? { accountId, items } : null;
  }

  private generateFallbackResponse(text: string): string {
    const lower = text.toLowerCase();
    if (/\bhello\b|\bhi\b|\bhey\b|\bgreetings\b/i.test(lower)) {
      return 'Hello! I\'m your B2B Auto Parts assistant. Here\'s what I can do:\n\n- **Check order status**: "Check ORD-001"\n- **Create order**: "Create order for ACC-001 with 10 BRK-001"\n- **Quick help**: "help"';
    }
    if (/\bhelp\b|\bwhat can you do\b|\boption\b/i.test(lower)) {
      return 'Here\'s what I can help with:\n\n📦 **Check Order Status**\nJust type the order number, e.g. "ORD-001"\n\n🛒 **Create a New Order**\nSay something like: "Create order for ACC-001 with 10 BRK-001 and 20 FLT-001"\n\n💡 **Quick commands**:\n- "Check status of ORD-001"\n- "Order 5 BRK-002 for ACC-002"\n- "What parts are available?"';
    }
    if (/\bpart\b|\binventory\b|\bavailable\b|\bcatalog\b|\bwhat do you have\b/i.test(lower)) {
      return 'You can browse the full parts catalog in the **Catalog** section from the sidebar. Each part shows SKU, price, and stock level.\n\nNeed to check a specific part? Just tell me the SKU.';
    }
    return 'I can help you with:\n\n- **Check order status**: Type an order number like "ORD-001"\n- **Create an order**: "Order 10 BRK-001 for ACC-001"\n- **Browse catalog**: Use the Catalog link in the sidebar\n\nWhat would you like to do?';
  }
}
