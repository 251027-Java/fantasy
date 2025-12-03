import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LuckScores } from "./components/luck-scores/luck-scores";

@Component({
	selector: 'app-root',
	imports: [RouterOutlet, LuckScores],
	templateUrl: './app.html',
	styleUrl: './app.css',
})
export class App {
	protected readonly title = signal('fantasy');
}
