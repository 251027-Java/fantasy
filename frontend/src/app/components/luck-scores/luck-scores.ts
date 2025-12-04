import { Component } from '@angular/core';
// biome-ignore lint/style/useImportType: idk what this warning is
import { Score, StatsResponse } from '../../interface/StatsResponse';
import { CommonModule } from '@angular/common';

@Component({
	selector: 'app-luck-scores',
	imports: [CommonModule],
	templateUrl: './luck-scores.html',
	styleUrl: './luck-scores.css',
})
export class LuckScores {
	private luckStats: StatsResponse;
	public sendLuckStats(leagueLuckStats: StatsResponse):void{
		this.luckStats = leagueLuckStats;
	}
	public getLuckStats(): StatsResponse{
		return this.luckStats;
	}

	readonly headers: [(keyof Score), string][] = [
		["totalLuck", "Total Luck"],
		["medLuck", "Median Luck"],
		["apLuck", "All Play Luck"],
		["apWins", "AP Wins"],
		["apLosses", "AP Losses"],
		["apTies", "AP Ties"],
		["wins", "Wins"],
		["losses", "Losses"],
		["ties", "Ties"]
	] as const

	nameWidth: string = "";
	statWidth: string = "";

	constructor(){
		this.luckStats = {stats:[]}
		this.generateDummyData(12);

		const numStats:number = this.headers.length
		this.nameWidth = `w-2/${numStats + 2}`
		this.statWidth = `w-1/${numStats + 2}`
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
					apLosses: numScores*i+4,
					apTies: numScores*i+5,
					wins: numScores*i+6,
					losses: numScores*i+7,
					ties: numScores*i+8
				}
			})
		}
	}
}
