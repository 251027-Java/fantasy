/** biome-ignore-all lint/style/useImportType: idk */
/** biome-ignore-all assist/source/organizeImports: whatever */
import { Component, ElementRef, NgZone, ViewChild } from '@angular/core';
import { Score } from '../../interface/stats-response';
import { CommonModule } from '@angular/common';
import { StatsService } from '../../services/stats-service';
import { ThemeService } from '../../services/theme-service';

export type luckStatColumn = keyof Score | 'name' | 'none';

@Component({
	selector: 'app-luck-scores',
	imports: [CommonModule],
	templateUrl: './luck-scores.html',
	styleUrl: './luck-scores.css',
})
export class LuckScores {
	statsService: StatsService;
	themeService: ThemeService;

	sortColumn: luckStatColumn = 'none';
	sortAsc: boolean = true;

	@ViewChild('scrollContainer') scrollContainer!: ElementRef<HTMLDivElement>;

	readonly headers: Map<keyof Score, string> = new Map<keyof Score, string>([
		['totalLuck', 'Total Luck'],
		['medLuck', 'Median Luck'],
		['apLuck', 'All Play Luck'],
		['apWins', 'AP Wins'],
		['apLoses', 'AP Losses'],
		['apTies', 'AP Ties'],
		['wins', 'Wins'],
		['loses', 'Losses'],
		['ties', 'Ties'],
	]);

	constructor(
		statsServ: StatsService,
		themeServ: ThemeService,
		private ngZone: NgZone,
	) {
		this.statsService = statsServ;
		this.themeService = themeServ;
	}

	getHeader(key: keyof any): string {
		return this.headers.get(key as keyof Score) || '';
	}

	getNumDataColumnsVisible(): number {
		if (!this.statsService.getStatsLoaded('Luck')) return this.headers.size;
		return this.statsService.getNumDataColumnsVisible('Luck');
	}

	toggleSort(column: luckStatColumn): void {
		if (!this.statsService.getStatsLoaded('Luck')) return;

		if (this.sortColumn === column) {
			this.sortAsc = !this.sortAsc;
		} else {
			this.sortColumn = column;
			this.sortAsc = true;
		}

		this.statsService.sortLuckStats(column, this.sortAsc);
	}

	getSortArrows(column: luckStatColumn): string[] {
		if (this.sortColumn === column) {
			return this.sortAsc ? ['▴'] : ['▾'];
		}
		return ['▴', '▾'];
	}

	getWidth(): number {
		if (this.statsService.getNumMembersVisible() === 0) return 90;
		return (
			7.5 * Math.min(12, this.statsService.getNumDataColumnsVisible('Luck') + 3)
		);
	}

	onMemberFilterChange(event: Event, member: string) {
		const input = event.target as HTMLInputElement;
		if (!input) return;

		const doc = document.documentElement;
		const offset = doc.scrollHeight - doc.scrollTop - doc.clientHeight;

		this.statsService.setMemberIsVisible(member, input.checked);

		this.ngZone.runOutsideAngular(() => {
			requestAnimationFrame(() => {
				doc.scrollTop = doc.scrollHeight - doc.clientHeight - offset;
			});
		});
	}
	onColumnFilterChange(event: Event, column: keyof Score) {
		const input = event.target as HTMLInputElement;
		if (input !== undefined)
			this.statsService.setColumnIsVisible('Luck', column, input.checked);
	}

	keepInsertionOrder = () => 0;
	caseInsensitiveSort = (a: any, b: any) => {
		return a.key.toLowerCase().localeCompare(b.key.toLowerCase());
	};
}
