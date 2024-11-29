import { Component } from '@angular/core';
import {generate} from "random-words";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'DHBW-T3INF4349-Verteilte-Systeme-Frontend';

  username;

  constructor() {
    const randomWords = generate({
      exactly: 2,
      wordsPerString: 1,
      formatter: (word) => word.slice(0, 1).toUpperCase().concat(word.slice(1)),
    });

    this.username = `${randomWords[0]}_${randomWords[1]}`;
  }


}
