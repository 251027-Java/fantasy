import { HttpClient } from '@angular/common/http';
import { Injectable, signal, WritableSignal } from '@angular/core';
import { toast } from 'ngx-sonner';
import { catchError, map, Observable, of } from 'rxjs';
import { luckStatColumn } from '../components/luck-scores/luck-scores';
import { Score, StatsResponse } from '../interface/stats-response';

export type StatsType = 'Luck';

@Injectable({
	providedIn: 'root',
})
export class StatsService {
	readonly numDecimalPlaces: number = 8;

	// Inject HttpClient for making HTTP requests
	constructor(private http: HttpClient) {}

	// holds the stats
	luckStatsResponse: WritableSignal<StatsResponse> = signal({ stats: [] });

	// filters for what is displayed
	filteredLeagueMembers: Map<string, boolean> = new Map<string, boolean>();
	filteredStats: Map<StatsType, Map<keyof any, boolean>> = new Map<
		StatsType,
		Map<keyof any, boolean>
	>();

	// Current league info is hardcoded for now
	// TODO: initialize these strings to "" for the final product
	private currentLeagueId: string = '1252005113573150720';
	private currentLeagueName: string = 'Rice League';

	private readonly columnWidthClassStrings: Record<number, string> = {
		1: 'w-1/3',
		2: 'w-1/4',
		3: 'w-1/5',
		4: 'w-1/6',
		5: 'w-1/7',
		6: 'w-1/8',
		7: 'w-1/9',
		8: 'w-1/10',
		9: 'w-1/11',
		10: 'w-1/12',
		11: 'w-1/12',
	} as const;

	// gets all the stats from the league
	getLeagueStats(): void {
		this.getLeagueLuckStats();
	}

	// Gets the league luck stats from the getLeagueStats function endpoint in the backend
	private getLeagueLuckStats(): void {
		// Recieve an observable after a get request to the spring backend. Map LeagueStatsDTO to stats-response interface
		let resp: Observable<StatsResponse> = this.http.get<StatsResponse>(
			`api/league/${this.currentLeagueId}/stats`,
		);

		// Pipe the data to map it to the StatsResponse interface
		resp = resp.pipe(
			map<any, StatsResponse>((data) => {
				// Map the received data to the StatsResponse interface
				const resp: StatsResponse = { stats: [] };
				for (const stat of data.stats) {
					// Push each stat into the stats array with proper formatting
					resp.stats.push({
						name: stat.userName,
						scores: {
							totalLuck: Number(
								stat.score.totalLuck.toFixed(this.numDecimalPlaces),
							),
							medLuck: Number(
								stat.score.medLuck.toFixed(this.numDecimalPlaces),
							),
							apLuck: Number(stat.score.apLuck.toFixed(this.numDecimalPlaces)),
							apWins: stat.score.apWins,
							apLoses: stat.score.apLoses,
							apTies: stat.score.apTies,
							wins: stat.score.wins,
							loses: stat.score.loses,
							ties: stat.score.ties,
						},
					});
				}

				// Return the resp object containing the mapped stats
				return resp;
			}),
			catchError(() => {
				this.displayError();
				return of({ stats: [] } as StatsResponse);
			}),
		);

		// Subscribe to the observable and set the luckStatsResponse signal with the received data
		resp.subscribe((data) => {
			try {
				// set the data
				this.luckStatsResponse.set(data);

				/* set the filters (all initialized to all true) */

				// filter for members of the league
				this.luckStatsResponse().stats.forEach((member) => {
					this.filteredLeagueMembers.set(member.name, true);
				});

				// filter for luck stats
				this.filteredStats.set(
					'Luck',
					new Map<keyof Score, boolean>(
						Object.keys(this.luckStatsResponse().stats[0].scores).map((k) => [
							k as keyof Score,
							true,
						]),
					),
				);
			} catch (_error) {
				this.displayError();
			}
		});
	}

	getDataColumnWidthClassString(numStatColumnWidths: number): string {
		return this.columnWidthClassStrings[numStatColumnWidths];
	}
	getNumDataColumnsVisible(statsType: StatsType): number {
		let numColumns = 0;
		const map: Map<keyof any, boolean> | undefined =
			this.filteredStats.get(statsType);
		if (map !== undefined) {
			for (const columnVisibility of map.values()) {
				if (columnVisibility) numColumns += 1;
			}
		}
		return numColumns;
	}
	getNumMembersVisible(): number {
		let numColumns = 0;
		for (const memberVisibility of this.filteredLeagueMembers.values()) {
			if (memberVisibility) numColumns += 1;
		}
		return numColumns;
	}

	getStatsLoaded(statType: StatsType): boolean {
		switch (statType) {
			case 'Luck':
				return this.luckStatsResponse().stats.length > 0;
			default:
				return false;
		}
	}

	setMemberIsVisible(member: string, isVisible: boolean): void {
		this.filteredLeagueMembers.set(member, isVisible);
	}
	setColumnIsVisible(
		statType: StatsType,
		columnName: keyof any,
		isVisible: boolean,
	): void {
		const map: Map<keyof any, boolean> | undefined =
			this.filteredStats.get(statType);
		if (map !== undefined) map.set(columnName, isVisible);
	}
	getMemberIsVisible(member: string): boolean {
		return this.filteredLeagueMembers.get(member) || false;
	}
	getColumnIsVisible(statType: StatsType, columnName: keyof any): boolean {
		if (!this.getStatsLoaded(statType)) return true;

		const map: Map<keyof any, boolean> | undefined =
			this.filteredStats.get(statType);
		if (map !== undefined) return map.get(columnName) || false;
		return false;
	}

	sortLuckStats(column: luckStatColumn, sortAsc: boolean) {
		if (column === ('name' as luckStatColumn)) {
			this.luckStatsResponse().stats.sort(
				(a, b) => a.name.localeCompare(b.name) * (sortAsc ? 1 : -1),
			);
		}

		this.luckStatsResponse().stats.sort((a, b) => {
			if (a.scores[column as keyof Score] < b.scores[column as keyof Score])
				return sortAsc ? -1 : 1;
			if (a.scores[column as keyof Score] > b.scores[column as keyof Score])
				return sortAsc ? 1 : -1;
			return 0;
		});
	}

	// Adding setters and getters to stats-service to store current league info
	setLeague(newLeagueId: string, newLeagueName: string) {
		if (newLeagueId !== this.currentLeagueId) {
			this.reset();

			this.currentLeagueId = newLeagueId;
			this.currentLeagueName = newLeagueName;

			//this.getLeagueStats();
		}
	}
	getCurrentLeagueId(): string {
		return this.currentLeagueId;
	}
	getCurrentLeagueName(): string {
		return this.currentLeagueName;
	}

	reset(): void {
		this.currentLeagueId = '';
		this.currentLeagueName = '';

		//reset all stats for a new league
		this.luckStatsResponse.set({ stats: [] });

		//reset all filters
		this.filteredLeagueMembers = new Map<string, boolean>();
		this.filteredStats = new Map<StatsType, Map<keyof any, boolean>>();
	}

	private displayError(): void {
		toast('Error loading stats for this league.', {
			action: {
				label: 'Close',
				onClick: () => {},
			},
			duration: Infinity,
		});
	}
}
