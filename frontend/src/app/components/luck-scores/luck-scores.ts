/** biome-ignore-all lint/style/useImportType: idk */
/** biome-ignore-all assist/source/organizeImports: whatever */
import { Component } from '@angular/core';
import { Score, StatsResponse } from '../../interface/StatsResponse';
import { CommonModule } from '@angular/common';
import { StatsService } from '../../services/stats-service';

@Component({
	selector: 'app-luck-scores',
	imports: [CommonModule],
	templateUrl: './luck-scores.html',
	styleUrl: './luck-scores.css',
})
export class LuckScores {
	luckStats: StatsResponse;

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

	constructor(private statsService: StatsService){
		this.luckStats = {stats:[]}
		this.generateDummyData(0);
	}

	ngOnInit(){
		this.luckStats = this.statsService.luckStatsResponse
		//console.log(this.luckStats)
	}

	private generateDummyData(num: number): void{
		const numScores: number = 9;
		for (let i=0; i<num; i++){
			let nm: string = ""
			for (let j=0; j<15; j++) nm += String.fromCharCode(97+i);
			this.luckStats.stats.push({
				name: nm,
				scores: {
					totalLuck: numScores*i,
					medLuck: numScores*i+1,
					apLuck: numScores*i+2,
					apWins: numScores*i+3,
					apLoses: numScores*i+4,
					apTies: numScores*i+5,
					wins: numScores*i+6,
					loses: numScores*i+7,
					ties: numScores*i+8
				}
			})
		}
	}
}
