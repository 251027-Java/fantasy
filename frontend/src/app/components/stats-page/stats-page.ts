/** biome-ignore-all lint/suspicious/noExplicitAny: any is fine */
// biome-ignore lint/style/useImportType: idk
// biome-ignore assist/source/organizeImports: whatever
import { Component, Type } from '@angular/core';
import { LuckScores } from '../luck-scores/luck-scores';
import { TestScores } from '../test-scores/test-scores';
import { NgComponentOutlet } from '@angular/common';

interface Tab {
  name: string;
  label: string;
  component: Type<any>;
  isActive: boolean;
}

@Component({
  selector: 'app-stats-page',
  imports: [NgComponentOutlet],
  templateUrl: './stats-page.html',
  styleUrl: './stats-page.css',
})
export class StatsPage {
  tabs: Tab[] = [
    { name: 'How lucky were you?', label: 'Luck Scores', component: LuckScores, isActive: false },
    { name: 'Testing...', label: 'Test Scores', component: TestScores, isActive: false }
  ];
  activeTab: Tab = this.tabs[0];
  displayedComponentTitle: string = "";

  currentLeague: string = "";

  constructor(){
    this.selectTab(this.tabs[0])
  }

  selectTab(tab: Tab) {
    this.activeTab.isActive = false;
    this.activeTab = tab;
    this.displayedComponentTitle = tab.name;
    tab.isActive = true;
  }

  backToLeagues() {

  }
}