/** biome-ignore-all lint/style/useImportType: idk */
/** biome-ignore-all assist/source/organizeImports: whatever */
import { Component } from '@angular/core';
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

	constructor(statsServ: StatsService, themeServ: ThemeService) {
		this.statsService = statsServ;
		this.themeService = themeServ;
	}

	getHeader(key: keyof any): string {
		return this.headers.get(key as keyof Score) || '';
	}

	toggleSort(column: luckStatColumn) {
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
		if (input !== undefined)
			this.statsService.setMemberIsVisible(member, input.checked);
	}
	onColumnFilterChange(event: Event, column: keyof Score) {
		const input = event.target as HTMLInputElement;
		if (input !== undefined)
			this.statsService.setColumnIsVisible('Luck', column, input.checked);
	}

	keepInsertionOrder = () => 0;
}
