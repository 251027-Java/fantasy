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
}

@Component({
  selector: 'app-stats-page',
  imports: [NgComponentOutlet],
  templateUrl: './stats-page.html',
  styleUrl: './stats-page.css',
})
export class StatsPage {
  tabs: Tab[] = [
    { name: 'Luck Scores', label: 'Luck', component: LuckScores },
    { name: 'Test Scores', label: 'Test', component: TestScores }
  ];
  activeTab: Tab = this.tabs[0];
  displayedComponentTitle: string = "";

  constructor(){
    this.selectTab(this.tabs[0])
  }

  selectTab(tab: Tab) {
    this.activeTab = tab;
    this.displayedComponentTitle = tab.name;
  }
}