import {Component, Input, OnInit} from '@angular/core';
import {WebsocketService} from '../services/websocket.service';
import {ChatMessage} from "../interfaces/chat-message";
import {HistoryService} from "../services/history.service";

@Component({
    selector: 'app-chat',
    templateUrl: './chat.component.html',
    styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit {
    @Input()
    username = '';

    message = '';
    messages: ChatMessage[] = [];

    constructor(private readonly websocketService: WebsocketService, private readonly historyService: HistoryService) {
    }

    ngOnInit(): void {
        // Get chat history from localStorage and missing messages from backend
        const history = localStorage.getItem("history") ?? "[]"

        this.messages = JSON.parse(history)

        this.historyService.getHistory(this.messages.at(0)?.timestamp ?? 0).subscribe(msgs => msgs.forEach((msg: any) => {
            this.messages.unshift(msg);
        }))
        this.saveHistory();

        // Subscribe to messages from the WebSocket server
        this.websocketService.subscribeToMessages().subscribe((msg) => {
            const messageBody: ChatMessage = JSON.parse(msg.body); // Assuming the message is in JSON format
            this.messages.unshift(messageBody);
            this.saveHistory();
        });
    }

    // Send a message using WebSocket
    sendMessage() {
        const msg = {
            name: this.username,
            content: this.message
        };
        this.websocketService.sendMessage(msg);
        this.message = '';
    }

    private saveHistory() {
        localStorage.setItem("history", JSON.stringify(this.messages));
    }

}
