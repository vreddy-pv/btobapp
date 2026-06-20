import { Injectable, NgZone, OnDestroy, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';

interface JsonRpcMessage {
  jsonrpc: string;
  id?: number | string;
  method?: string;
  params?: unknown;
  result?: unknown;
  error?: { code: number; message: string };
}

@Injectable({ providedIn: 'root' })
export class McpClientService implements OnDestroy {
  private readonly mcpBase = 'http://localhost:8080/mcp';
  private eventSource: EventSource | null = null;
  private sessionId = signal<string | null>(null);
  private messageSubject = new Subject<JsonRpcMessage>();
  private requestId = 0;
  private pending = new Map<string, { resolve: (v: unknown) => void; reject: (e: Error) => void }>();

  readonly connected = signal(false);
  messages$ = this.messageSubject.asObservable();

  constructor(private http: HttpClient, private zone: NgZone) {}

  connect(): void {
    if (this.eventSource) return;

    this.zone.runOutsideAngular(() => {
      this.eventSource = new EventSource(`${this.mcpBase}/sse`);

      this.eventSource.addEventListener('endpoint', (event: MessageEvent) => {
        const url = new URL(event.data, window.location.origin);
        const sid = url.searchParams.get('sessionId');
        if (sid) {
          this.sessionId.set(sid);
          this.connected.set(true);
          this.sendInitialize();
        }
      });

      this.eventSource.addEventListener('message', (event: MessageEvent) => {
        this.zone.run(() => {
          try {
            const msg: JsonRpcMessage = JSON.parse(event.data);
            this.messageSubject.next(msg);

            if (msg.id !== undefined) {
              const key = String(msg.id);
              const pending = this.pending.get(key);
              if (pending) {
                this.pending.delete(key);
                if (msg.error) {
                  pending.reject(new Error(msg.error.message));
                } else {
                  pending.resolve(msg.result);
                }
              }
            }
          } catch {}
        });
      });

      this.eventSource.onerror = () => {
        this.zone.run(() => {
          this.connected.set(false);
          this.eventSource?.close();
          this.eventSource = null;
        });
      };
    });
  }

  private sendInitialize(): void {
    this.sendRequest('initialize', {
      protocolVersion: '2025-03-26',
      capabilities: {},
      clientInfo: { name: 'btob-chat-ui', version: '1.0.0' },
    }).then(() => {
      this.sendRequest('notifications/initialized', {});
    });
  }

  async listTools(): Promise<unknown[]> {
    const result = await this.sendRequest('tools/list', {}) as { tools: unknown[] };
    return result.tools;
  }

  async callTool(name: string, arguments_: Record<string, unknown>): Promise<unknown> {
    const result = await this.sendRequest('tools/call', {
      name,
      arguments: arguments_,
    }) as { content: { type: string; text: string }[] };
    return result;
  }

  private sendRequest(method: string, params: unknown): Promise<unknown> {
    const id = ++this.requestId;
    return new Promise((resolve, reject) => {
      const sid = this.sessionId();
      if (!sid) { reject(new Error('Not connected')); return; }

      this.pending.set(String(id), { resolve, reject });

      this.http.post(`${this.mcpBase}/message?sessionId=${sid}`, {
        jsonrpc: '2.0',
        id,
        method,
        params,
      }).subscribe({ error: (err) => reject(err) });
    });
  }

  disconnect(): void {
    this.eventSource?.close();
    this.eventSource = null;
    this.connected.set(false);
    this.sessionId.set(null);
  }

  ngOnDestroy(): void {
    this.disconnect();
  }
}
