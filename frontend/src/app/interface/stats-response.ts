export interface StatsResponse {
	stats: Stats[];
	weeklyMedianLuck: WeeklyMedianLuck[];
}

// weekly median luck has the username and the array of median luck scores for each week
export interface WeeklyMedianLuck {
	userName: string;
	stats: number[];
}

export interface Stats {
	name: string;
	scores: Score;
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
