export interface LoginResponse {
	userId: string;
	leagues: League[];
}

export interface League {
	id: string;
	name: string;
}
