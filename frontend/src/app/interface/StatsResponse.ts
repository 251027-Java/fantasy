export interface StatsResponse {
	stats: Stats[];
}

export interface Stats {
	name: string;
	scores: Score[];
}

export interface Score {
	totalLuck: number;
	medLuck: number;
	apLuck: number;
	apWins: number;
	apLosses: number;
	apTies: number;
	wins: number;
	losses: number;
	ties: number;
}
