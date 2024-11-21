import {Injectable} from '@angular/core';
import {Client, Message} from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class WebsocketService {
    private readonly client!: Client;

    constructor() {
        const hostname = "localhost"
        console.log("Hostname: " + hostname)
        this.client = new Client({
            brokerURL: 'ws://' + hostname + '/chat', // WebSocket endpoint
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            webSocketFactory: () => {
                return new SockJS('http://' + hostname + '/chat'); // SockJS endpoint
            }
        });

        this.client.activate();
        this.client.logRawCommunication = true
    }

    // Subscribe to messages sent to the /topic/messages
    subscribeToMessages(): Observable<Message> {
        return new Observable<Message>(observer => {
            this.client.onConnect = () => {
                this.client.subscribe('/topic/messages', message => {
                    observer.next(message);
                });

                // sobald der client connected, wird der chatverlauf geladen
                this.client.subscribe("/app/history", message => {
                    console.log("Received message: " + message.body);
                    observer.next(message);
                })
            };
        });
    }

    // Send a message to /app/message
    sendMessage(msg: any) {
        this.client.publish({
            destination: '/app/message',
            body: JSON.stringify(msg)
        });
    }

    disconnect() {
        if (this.client) {
            this.client.deactivate();
        }
    }
}