import {Component, Input, OnInit} from '@angular/core';
import {ChatMessage} from "../interfaces/chat-message";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-message',
  standalone: true,
  imports: [
    NgClass
  ],
  templateUrl: './message.component.html',
  styleUrl: './message.component.scss'
})
export class MessageComponent implements OnInit {

  @Input()
  message!: ChatMessage;

  @Input()
  currentUser!: string;

  text = "";

  cssClass: string = "";

  ngOnInit(): void {
    this.text = this.message.name + ": " + this.message.content;

    switch(this.message.name) {
      case this.currentUser: this.cssClass = "my-message"; break;
      case "System": this.cssClass = "system-message"; break;
    }
  }
}
