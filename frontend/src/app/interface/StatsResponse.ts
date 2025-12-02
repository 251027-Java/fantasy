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
	apLoses: number;
	apTies: number;
	wins: number;
	loses: number;
	ties: number;
}
