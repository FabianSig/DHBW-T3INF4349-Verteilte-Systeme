import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ChatMessage} from "../interfaces/chat-message";
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class HistoryService {

  constructor(private readonly httpClient: HttpClient) { }

    /**
     * Get chat history after the timestamp of the last message saved in localStorage
     * @param timestamp
     */
    getHistory(timestamp: number) {
        return this.httpClient.get<ChatMessage[]>(`http://${environment.hostname}/history`, {params: {timestamp: timestamp}});
    }
}
