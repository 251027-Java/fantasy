/** biome-ignore-all lint/style/useImportType: idk */
/** biome-ignore-all assist/source/organizeImports: whatever */
import { Component } from '@angular/core';
import { Score } from '../../interface/stats-response';
import { CommonModule } from '@angular/common';
import { StatsService } from '../../services/stats-service';
import { ThemeService } from '../../services/theme-service';

type luckStatColumn = keyof Score | "name" | "none";

@Component({
	selector: 'app-luck-scores',
	imports: [CommonModule],
	templateUrl: './luck-scores.html',
	styleUrl: './luck-scores.css',
})
export class LuckScores {
	statsService: StatsService;
	themeService: ThemeService;

	sortColumn: luckStatColumn = "none";
  	sortAsc: boolean = true;

	readonly headers: [(keyof Score), string][] = [
		["totalLuck", "Total Luck"],
		["medLuck", "Median Luck"],
		["apLuck", "All Play Luck"],
		["apWins", "AP Wins"],
		["apLoses", "AP Losses"],
		["apTies", "AP Ties"],
		["wins", "Wins"],
		["loses", "Losses"],
		["ties", "Ties"]
	] as const

	constructor(statsServ: StatsService, themeServ: ThemeService){
		this.statsService = statsServ
		this.themeService = themeServ
	}

	toggleSort(column: luckStatColumn) {
		if (this.sortColumn === column) {
			this.sortAsc = !this.sortAsc;
		} else {
			this.sortColumn = column;
			this.sortAsc = true;
		}
	}

	getSortArrows(column: luckStatColumn): string[] {
		if (this.sortColumn === column) {
			return this.sortAsc ? ['▴'] : ['▾'];
		}
		return ['▴', '▾'];
	}
}
