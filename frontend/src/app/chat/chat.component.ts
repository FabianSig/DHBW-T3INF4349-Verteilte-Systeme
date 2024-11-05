import { Component, OnInit } from '@angular/core';
import { WebsocketService } from '../services/websocket.service';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit {
  message = '';
  messages: string[] = [];

  constructor(private websocketService: WebsocketService) { }

  ngOnInit(): void {
    // Subscribe to messages from the WebSocket server
    this.websocketService.subscribeToMessages().subscribe((msg) => {
      const messageBody = JSON.parse(msg.body); // Assuming the message is in JSON format
      this.messages.push(`${messageBody.name}: ${messageBody.content}`);
    });
  }

  // Send a message using WebSocket
  sendMessage() {
    const msg = {
      name: 'User',
      content: this.message
    };
    this.websocketService.sendMessage(msg);
    this.message = '';
  }
}
