import { Component } from '@angular/core';
// biome-ignore lint/style/useImportType: idk what this warning is
import { Score, StatsResponse } from '../../interface/StatsResponse';

@Component({
	selector: 'app-luck-scores',
	imports: [],
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

	constructor(){
		this.luckStats = {stats:[
			{
				name: "a",
				scores: {
					totalLuck: 0,
					medLuck: 1,
					apLuck: 2,
					apWins: 3,
					apLosses: 4,
					apTies: 5,
					wins: 6,
					losses: 7,
					ties: 8
				}
			},
			{
				name: "b",
				scores: {
					totalLuck: 9,
					medLuck: 10,
					apLuck: 11,
					apWins: 12,
					apLosses: 13,
					apTies: 14,
					wins: 15,
					losses: 16,
					ties: 17
				}
			},
			{
				name: "c",
				scores: {
					totalLuck: 18,
					medLuck: 19,
					apLuck: 20,
					apWins: 21,
					apLosses: 22,
					apTies: 23,
					wins: 24,
					losses: 25,
					ties: 26
				}
			}
		]}
	}
}
