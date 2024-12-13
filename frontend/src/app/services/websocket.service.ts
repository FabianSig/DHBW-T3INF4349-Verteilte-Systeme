import {Injectable} from '@angular/core';
import {Client, Message} from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import {Observable} from 'rxjs';
import {ChatMessage} from "../interfaces/chat-message";
import {environment} from "../../environments/environment";

@Injectable({
    providedIn: 'root'
})
export class WebsocketService {
    private readonly client!: Client;

    constructor() {
        const hostname = environment.hostname
        console.log("Hostname: " + hostname)
        this.client = new Client({
            brokerURL: 'ws://' + hostname + '/chat', // WebSocket endpoint
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            // http fallback in case of WebSocket failure
            webSocketFactory: () => {
                return new SockJS('http://' + hostname + '/chat'); // SockJS endpoint
            }
        });

        this.client.activate();

        // if the websocket server fails, reload the page to establish a new connection and automatically fetch missing messages
        this.client.onWebSocketClose = () => {
            let retryCount = parseInt(localStorage.getItem('retry') ?? "0");
            localStorage.setItem('retry', "" + ++retryCount);

            if (retryCount === 3) {
                alert("The server is not responding. Please try again later.");
            }

            // to avoid spam of the reload, add a delay when retrying more than 2 times
            setTimeout(() => {
                location.reload();
            }, 3000 + 10000 * (retryCount % (retryCount - 1)));
        }

    }

    /** Subscribe to messages sent to the /topic/messages
     * @returns Observable<Message>
     */
    subscribeToMessages()
        :
        Observable<Message> {
        return new Observable<Message>(observer => {

            this.client.onConnect = () => {
                localStorage.removeItem('retry');

                this.client.subscribe('/topic/messages', message => {
                    observer.next(message);
                });
            };
        });
    }

    /** Send a message to the topic /app/message.
     *
     * @param msg
     */
    sendMessage(msg
                :
                ChatMessage
    ) {
        this.client.publish({
            destination: '/app/message',
            body: JSON.stringify(msg)
        });
    }

}