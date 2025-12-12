/** biome-ignore-all lint/style/useImportType: idk */
/** biome-ignore-all assist/source/organizeImports: whatever */
import { Component, ElementRef, HostListener, NgZone, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatsService } from '../../services/stats-service';
import { ThemeService } from '../../services/theme-service';

export type medianLuckStatColumn = number | 'name' | 'none';

@Component({
	selector: 'app-luck-scores',
	imports: [CommonModule],
	templateUrl: './median-luck-scores.html',
	styleUrl: './median-luck-scores.css',
})
export class MedianLuckScores {
	statsService: StatsService;
	themeService: ThemeService;

	sortColumn: medianLuckStatColumn = 'none';
	sortAsc: boolean = true;

	@ViewChild('scrollContainer') scrollContainer!: ElementRef<HTMLDivElement>;

	constructor(
		statsServ: StatsService,
		themeServ: ThemeService,
		private ngZone: NgZone,
	) {
		this.statsService = statsServ;
		this.themeService = themeServ;
	}

  windowWidth: number = 0;
  @HostListener('window:resize', ['$event'])
  getScreenSize(_event?: any): void {
    this.windowWidth = window.innerWidth;
  }

	getNumDataColumnsVisible(): number {
		if (!this.statsService.getStatsLoaded('MedianLuck')) return 0;
		return this.statsService.getNumDataColumnsVisible('MedianLuck');
	}

	toggleSort(column: medianLuckStatColumn): void {
		if (!this.statsService.getStatsLoaded('MedianLuck')) return;

		if (this.sortColumn === column) {
			this.sortAsc = !this.sortAsc;
		} else {
			this.sortColumn = column;
			this.sortAsc = true;
		}

		this.statsService.sortMedianLuckStats(column, this.sortAsc);
	}

	getSortArrows(column: medianLuckStatColumn): string[] {
		if (this.sortColumn === column) {
			return this.sortAsc ? ['▴'] : ['▾'];
		}
		return ['▴', '▾'];
	}

	getWidth(): number {
		if (this.statsService.getNumMembersVisible() === 0) return 90;
		return (
			7.5 * Math.min(12, this.statsService.getNumDataColumnsVisible('MedianLuck') + 3)
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
	onColumnFilterChange(event: Event, column: number) {
		const input = event.target as HTMLInputElement;
		if (input !== undefined)
			this.statsService.setColumnIsVisible('MedianLuck', column, input.checked);
	}

	keepInsertionOrder = () => 0;
	caseInsensitiveSort = (a: any, b: any) => {
		return a.key.toLowerCase().localeCompare(b.key.toLowerCase());
	};
}
