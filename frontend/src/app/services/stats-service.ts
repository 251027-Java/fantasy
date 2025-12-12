import { HttpClient } from '@angular/common/http';
import { Injectable, signal, WritableSignal } from '@angular/core';
import { toast } from 'ngx-sonner';
import { catchError, map, Observable, of } from 'rxjs';
import { luckStatColumn } from '../components/luck-scores/luck-scores';
import { medianLuckStatColumn } from '../components/median-luck-scores/median-luck-scores';
import { Score, StatsResponse } from '../interface/stats-response';
import { AuthService } from './auth-service';

export type StatsType = 'Luck' | 'MedianLuck';

@Injectable({
	providedIn: 'root',
})
export class StatsService {
	readonly numDecimalPlaces: number = 2;

	// Inject HttpClient for making HTTP requests
	constructor(
		private http: HttpClient,
		private authService: AuthService,
	) {}

	// holds the stats
	statsResponse: WritableSignal<StatsResponse> = signal({
		stats: [],
		weeklyMedianLuck: [],
	});

	// filters for what is displayed
	filteredLeagueMembers: Map<string, boolean> = new Map<string, boolean>();
	filteredStats: Map<StatsType, Map<keyof any, boolean>> = new Map<
		StatsType,
		Map<keyof any, boolean>
	>();

	private currentLeagueId: string = '';
	private currentLeagueName: string = '';

	private readonly nameColumnWidthClassStrings: Record<number, string> = {
		0: 'w-[100%]',
		1: 'w-2/3',
		2: 'w-2/4',
		3: 'w-2/5',
		4: 'w-2/6',
		5: 'w-2/7',
		6: 'w-2/8',
		7: 'w-2/9',
		8: 'w-2/10',
		9: 'w-2/11',
		10: 'w-2/12',
		11: 'min-w-2/12',
		12: 'min-w-2/12',
		13: 'min-w-2/12',
		14: 'min-w-2/12',
		15: 'min-w-2/12',
		16: 'min-w-2/12',
		17: 'min-w-2/12',
		18: 'min-w-2/12',
	} as const;
	private readonly columnWidthClassStrings: Record<number, string> = {
		0: 'w-[0%]',
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
		11: 'min-w-1/12',
		12: 'min-w-1/12',
		13: 'min-w-1/12',
		14: 'min-w-1/12',
		15: 'min-w-1/12',
		16: 'min-w-1/12',
		17: 'min-w-1/12',
		18: 'min-w-1/12',
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
			{
				headers: {
					Authorization: `Bearer ${this.authService.getToken()}`,
				},
			},
		);

		// Pipe the data to map it to the StatsResponse interface
		console.log(resp);
		resp = resp.pipe(
			map<any, StatsResponse>((data) => {
				// Map the received data to the StatsResponse interface
				const resp: StatsResponse = { stats: [], weeklyMedianLuck: [] };
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

				//resp.weeklyMedianLuck = data.weeklyMedianLuck;
				for (const medPlayer of data.weeklyMedianLuck) {
					// Push each median stat into the stats array with proper formatting
					resp.weeklyMedianLuck.push({
						userName: medPlayer.userName,
						stats: medPlayer.stats.map((luck: number) =>
							Number(luck.toFixed(this.numDecimalPlaces)),
						),
					});
				}

				// Return the resp object containing the mapped stats
				return resp;
			}),
			catchError(() => {
				this.displayStatsLoadingError();
				return of({ stats: [], weeklyMedianLuck: [] } as StatsResponse);
			}),
		);

		// Subscribe to the observable and set the statsResponse signal with the received data
		resp.subscribe((data) => {
			try {
				// set the data
				this.statsResponse.set(data);

				/* set the filters (all initialized to all true) */

				// filter for members of the league
				this.statsResponse().stats.forEach((member) => {
					this.filteredLeagueMembers.set(member.name, true);
				});

				// filter for luck stats
				this.filteredStats.set(
					'Luck',
					new Map<keyof Score, boolean>(
						Object.keys(this.statsResponse().stats[0].scores).map((k) => [
							k as keyof Score,
							true,
						]),
					),
				);

				// filter for median luck stats
				this.filteredStats.set(
					'MedianLuck',
					new Map<number, boolean>(
						this.statsResponse().weeklyMedianLuck[0].stats.map((val, idx) => [
							idx + 1,
							true,
						]),
					),
				);
			} catch (_error) {
				this.displayStatsLoadingError();
			}
		});
	}

	getNameColumnWidthClassString(numStatColumnWidths: number): string {
		return this.nameColumnWidthClassStrings[numStatColumnWidths];
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
				return this.statsResponse().stats.length > 0;
			case 'MedianLuck':
				return this.statsResponse().weeklyMedianLuck.length > 0;
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
			this.statsResponse().stats.sort(
				(a, b) => a.name.localeCompare(b.name) * (sortAsc ? 1 : -1),
			);
			return;
		}

		this.statsResponse().stats.sort((a, b) => {
			if (a.scores[column as keyof Score] < b.scores[column as keyof Score])
				return sortAsc ? -1 : 1;
			if (a.scores[column as keyof Score] > b.scores[column as keyof Score])
				return sortAsc ? 1 : -1;
			return 0;
		});
	}
	sortMedianLuckStats(column: medianLuckStatColumn, sortAsc: boolean) {
		if (column === ('name' as medianLuckStatColumn)) {
			this.statsResponse().weeklyMedianLuck.sort(
				(a, b) => a.userName.localeCompare(b.userName) * (sortAsc ? 1 : -1),
			);
			return;
		}

		this.statsResponse().weeklyMedianLuck.sort((a, b) => {
			if (a.stats[(column as number) - 1] < b.stats[(column as number) - 1])
				return sortAsc ? -1 : 1;
			if (a.stats[(column as number) - 1] > b.stats[(column as number) - 1])
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

			this.getLeagueStats();
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
		this.statsResponse.set({ stats: [], weeklyMedianLuck: [] });

		//reset all filters
		this.filteredLeagueMembers = new Map<string, boolean>();
		this.filteredStats = new Map<StatsType, Map<keyof any, boolean>>();
	}

	displayStatsLoadingError(): void {
		toast('Error loading stats for this league.', {
			action: {
				label: 'Close',
				onClick: () => {},
			},
			duration: Infinity,
		});
	}
}
