import { Component, OnInit, signal, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { McpClientService } from '../services/mcp-client.service';
import { AgentService, ChatMessage } from '../services/agent.service';

@Component({
  selector: 'app-chat',
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.html',
})
export class Chat implements OnInit, AfterViewChecked {
  @ViewChild('chatContainer') private chatContainer!: ElementRef;

  inputText = signal('');
  open = signal(false);

  constructor(
    readonly mcp: McpClientService,
    readonly agent: AgentService,
  ) {}

  ngOnInit(): void {
    this.mcp.connect();
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  async send(): Promise<void> {
    const text = this.inputText().trim();
    if (!text || this.agent.processing()) return;
    this.inputText.set('');
    await this.agent.sendMessage(text);
  }

  async quickAction(text: string): Promise<void> {
    this.inputText.set(text);
    await this.send();
  }

  toggle(): void {
    this.open.update(v => !v);
  }

  private scrollToBottom(): void {
    try {
      this.chatContainer.nativeElement.scrollTop = this.chatContainer.nativeElement.scrollHeight;
    } catch {}
  }
}
