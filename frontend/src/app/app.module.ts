import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ChatComponent } from './chat/chat.component';
import {FormsModule} from "@angular/forms";
import { WebsocketService } from './services/websocket.service';
import {MessageComponent} from "./message/message.component";

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
  providers: [WebsocketService],
  bootstrap: [AppComponent]
})
export class AppModule { }
