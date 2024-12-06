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
  isMine!: boolean;

  text = "";

  ngOnInit(): void {
    this.text = this.message.name + ": " + this.message.content;
  }
}
