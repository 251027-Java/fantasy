/** biome-ignore-all assist/source/organizeImports: <whatever> */
import { NgClass, NgComponentOutlet } from '@angular/common';
import { Component, Type } from '@angular/core';
import { Router } from '@angular/router';
import { StatsService } from '../../services/stats-service';
import { ThemeService } from '../../services/theme-service';
import { LuckScores } from '../luck-scores/luck-scores';
import { MedianLuckScores } from '../median-luck-scores/median-luck-scores';
import { HlmToaster } from '@spartan-ng/helm/sonner';

interface Tab {
	namePrebold: string;
	nameBold: string;
	namePostbold: string;
	label: string;
	component: Type<any>;
	isActive: boolean;
	imageBackgroundClass: string;
}

@Component({
	selector: 'app-stats-page',
	imports: [NgComponentOutlet, NgClass, HlmToaster],
	templateUrl: './stats-page.html',
	styleUrl: './stats-page.css',
})
export class StatsPage {
	themeService: ThemeService;

	tabs: Tab[] = [
		{
			namePrebold: 'How ',
			nameBold: 'lucky',
			namePostbold: ' were you?',
			label: 'Luck Scores',
			component: LuckScores,
			isActive: false,
			imageBackgroundClass: 'luckstatsbackground',
		},
		{
			namePrebold: 'You\'re ',
			nameBold: 'mid',
			namePostbold: ' af.',
			label: 'Median Scores',
			component: MedianLuckScores,
			isActive: false,
			imageBackgroundClass: 'medianluckstatsbackground',
		},
	];
	activeTab: Tab = this.tabs[0];
	displayedComponentTitlePrebold: string = '';
	displayedComponentTitleBold: string = '';
	displayedComponentTitlePostbold: string = '';
	imageBackgroundClass: string = '';

	currentLeagueName: string = '';

	constructor(
		private router: Router,
		private statsService: StatsService,
		themeServ: ThemeService,
	) {
		this.themeService = themeServ;
	}

	selectTab(tab: Tab) {
		this.activeTab.isActive = false;
		this.activeTab = tab;
		this.displayedComponentTitlePrebold = tab.namePrebold;
		this.displayedComponentTitleBold = tab.nameBold;
		this.displayedComponentTitlePostbold = tab.namePostbold;
		this.imageBackgroundClass = tab.imageBackgroundClass;
		tab.isActive = true;
	}

	backToLeagues() {
		this.router.navigateByUrl('/league');
	}

	ngOnInit() {
		this.currentLeagueName = this.statsService.getCurrentLeagueName();

		this.selectTab(this.tabs[0]);

		if (this.statsService.getCurrentLeagueId() === '')
			this.statsService.displayStatsLoadingError();
	}
}
