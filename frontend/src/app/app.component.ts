import {Component} from '@angular/core';
import {generate} from "random-words";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent {
    title = 'DHBW-T3INF4349-Verteilte-Systeme-Frontend';

    username: string;

    constructor() {
        this.username = localStorage.getItem("username") ?? this.generateName();
        localStorage.setItem("username", this.username);
    }

    private generateName(): string {
        const randomWords = <string>generate({
            exactly: 2,
            wordsPerString: 1,
            formatter: (word) => word.slice(0, 1).toUpperCase().concat(word.slice(1)),
        });

        return `${randomWords[0]}_${randomWords[1]}`;
    }

}
