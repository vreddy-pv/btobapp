import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { lastValueFrom } from 'rxjs';

export interface ChatMessage {
  role: 'user' | 'assistant' | 'system' | 'tool';
  content: string;
}

@Injectable({ providedIn: 'root' })
export class AgentService {
  private readonly agentUrl = 'http://localhost:8000';

  messages = signal<ChatMessage[]>([]);
  processing = signal(false);

  constructor(private http: HttpClient) {}

  async sendMessage(userText: string): Promise<void> {
    this.messages.update(m => [...m, { role: 'user', content: userText }]);
    this.processing.set(true);

    try {
      const result = await lastValueFrom(
        this.http.post<{ response: string }>(`${this.agentUrl}/chat`, { message: userText })
      );

      this.messages.update(m => [...m, { role: 'assistant', content: result.response }]);
    } catch {
      this.messages.update(m => [...m, {
        role: 'assistant',
        content: this.generateFallbackResponse(userText),
      }]);
    } finally {
      this.processing.set(false);
    }
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
