import { Component, signal, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AgentService, ChatMessage } from '../services/agent.service';

@Component({
  selector: 'app-chat',
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.html',
})
export class Chat implements AfterViewChecked {
  @ViewChild('chatContainer') private chatContainer!: ElementRef;

  inputText = signal('');
  open = signal(false);

  constructor(
    readonly agent: AgentService,
  ) {}

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
