import {Component, Input, OnInit} from '@angular/core';
import {WebsocketService} from '../services/websocket.service';
import {of} from "rxjs";

@Component({
    selector: 'app-chat',
    templateUrl: './chat.component.html',
    styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit {
    @Input()
    username = '';

    message = '';
    messages: string[] = [];

    constructor(private readonly websocketService: WebsocketService) {
    }

    ngOnInit(): void {
        // Subscribe to messages from the WebSocket server
        this.websocketService.subscribeToMessages().subscribe((msg) => {
            //falls es ein array ist, anders parsen
            if (msg.body.includes("[")) {
                const messageBody = JSON.parse(msg.body); // Assuming the message is in JSON format
                this.messages = [];
                messageBody.forEach((msg: any) => {
                    //TODO hier objekt kreieren
                    this.messages.push(`${msg.name}: ${msg.content}`);
                });
                return;
            }
            const messageBody = JSON.parse(msg.body); // Assuming the message is in JSON format
            this.messages.push(`${messageBody.name}: ${messageBody.content}`);
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

    protected readonly of = of;
}
