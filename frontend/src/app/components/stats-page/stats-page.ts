/** biome-ignore-all lint/suspicious/noExplicitAny: any is fine */
/** biome-ignore-all lint/style/useImportType: idk */
// biome-ignore assist/source/organizeImports: whatever
import { Component, Type } from '@angular/core';
import { LuckScores } from '../luck-scores/luck-scores';
import { TestScores } from '../test-scores/test-scores';
import { NgComponentOutlet, NgClass } from '@angular/common';
import { Router } from '@angular/router';
import { StatsService } from '../../services/stats-service';
import { ThemeService } from '../../services/theme-service';

interface Tab {
  namePrebold: string;
  nameBold: string;
  namePostbold: string;
  label: string;
  component: Type<any>;
  isActive: boolean;
}

@Component({
  selector: 'app-stats-page',
  imports: [NgComponentOutlet, NgClass],
  templateUrl: './stats-page.html',
  styleUrl: './stats-page.css',
})
export class StatsPage {
  themeService: ThemeService;

  tabs: Tab[] = [
    { namePrebold: 'How ', nameBold: 'lucky', namePostbold: ' were you?', label: 'Luck Scores', component: LuckScores, isActive: false },
    { namePrebold: '', nameBold: 'Testing', namePostbold: '...', label: 'Test Scores', component: TestScores, isActive: false }
  ];
  activeTab: Tab = this.tabs[0];
  displayedComponentTitlePrebold: string = "";
  displayedComponentTitleBold: string = "";
  displayedComponentTitlePostbold: string = "";

  currentLeagueName: string = "";

  constructor(private router: Router, private statsService: StatsService, themeServ: ThemeService){
    this.themeService = themeServ
  }

  selectTab(tab: Tab) {
    this.activeTab.isActive = false;
    this.activeTab = tab;
    this.displayedComponentTitlePrebold = tab.namePrebold;
    this.displayedComponentTitleBold = tab.nameBold;
    this.displayedComponentTitlePostbold = tab.namePostbold;
    tab.isActive = true;
  }

  backToLeagues() {
    this.router.navigateByUrl("/league")
  }

  ngOnInit(){
    this.currentLeagueName = this.statsService.currentLeagueName;

    this.statsService.getLeagueLuckStats();
    this.selectTab(this.tabs[0])
  }
}