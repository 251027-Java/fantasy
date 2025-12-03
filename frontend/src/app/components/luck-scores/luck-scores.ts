// biome-ignore assist/source/organizeImports: idk and idc
import { Component } from '@angular/core';
import { Score, StatsResponse } from '../../interface/StatsResponse';
import { KeyValuePipe } from '@angular/common';

@Component({
	selector: 'app-luck-scores',
	imports: [KeyValuePipe],
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

	readonly headers: Map<(keyof Score), string> = new Map<(keyof Score), string>([
		["totalLuck", "Total Luck"],
		["medLuck", "Median Luck"],
		["apLuck", "All Play Luck"],
		["apWins", "AP Wins"],
		["apLosses", "AP Losses"],
		["apTies", "AP Ties"],
		["wins", "Wins"],
		["losses", "Losses"],
		["ties", "Ties"]
	] as const)
}
