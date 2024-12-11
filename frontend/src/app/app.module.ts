import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ChatComponent} from './chat/chat.component';
import {FormsModule} from "@angular/forms";
import {WebsocketService} from './services/websocket.service';
import {MessageComponent} from "./message/message.component";
import {HistoryService} from "./services/history.service";
import {provideHttpClient} from "@angular/common/http";

@NgModule({
    declarations: [
        AppComponent,
        ChatComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        MessageComponent,
    ],
    providers: [WebsocketService, HistoryService, provideHttpClient()],
    bootstrap: [AppComponent]
})
export class AppModule {
}
