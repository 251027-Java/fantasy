/** biome-ignore-all lint/style/useImportType: idk */
/** biome-ignore-all assist/source/organizeImports: whatever */
import { Component } from '@angular/core';
import { Score } from '../../interface/stats-response';
import { CommonModule } from '@angular/common';
import { StatsService } from '../../services/stats-service';
import { ThemeService } from '../../services/theme-service';

@Component({
	selector: 'app-luck-scores',
	imports: [CommonModule],
	templateUrl: './luck-scores.html',
	styleUrl: './luck-scores.css',
})
export class LuckScores {
	statsService: StatsService;
	themeService: ThemeService;

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
}
