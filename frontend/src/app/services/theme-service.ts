import { Injectable, signal, WritableSignal } from '@angular/core';
import { Theme, ThemeColor } from '../interface/theme';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  statsTheme: WritableSignal<ThemeColor> = signal("blue");
  
  getThemeColors(): ThemeColor[]{
    return this.colorOrder;
  }
  getThemeClassString(themeColor: ThemeColor, themeClass: keyof Theme): string{
    return this.statsThemes[themeColor][themeClass];
  }
  getCurrentThemeClassString(themeClass: keyof Theme): string{
    return this.statsThemes[this.statsTheme()][themeClass];
  }

  setStatsTheme(themeColor: ThemeColor): void{
    this.statsTheme.set(themeColor);
  }

  private readonly colorOrder: ThemeColor[] = ["red", "orange", "yellow", "green", "blue", "violet"] as const
  private readonly statsThemes: Record<ThemeColor, Theme> = {
    "red": {
      selectThemeButtonClasses: "border-red-700 bg-red-500 hover:bg-red-700",
      backToLeaguesButtonClasses: "border-red-700 bg-red-900 hover:bg-red-950",
      displayedComponentTitleBoldClass: "text-red-600",
      selectedTabButtonClasses: "border-red-700 bg-red-600 hover:bg-red-700",

      headerTableClass: "bg-red-200 hover:bg-red-300",
      bodyTableClass: "bg-red-50",   
      headerNameClass: "bg-red-800 hover:bg-red-900",
      loadingTableClass: "text-red-900",
      loadingTableBackgroundClass: "bg-[radial-gradient(circle_at_top_right,_#fff5f6,_#ffefef,_#fff7f8)]",
    },
    "orange": {
      selectThemeButtonClasses: "border-orange-700 bg-orange-500 hover:bg-orange-700",
      backToLeaguesButtonClasses: "border-orange-700 bg-orange-900 hover:bg-orange-950",
      displayedComponentTitleBoldClass: "text-orange-600",
      selectedTabButtonClasses: "border-orange-700 bg-orange-600 hover:bg-orange-700",

      headerTableClass: "bg-orange-200 hover:bg-orange-300",
      bodyTableClass: "bg-orange-50",   
      headerNameClass: "bg-orange-800 hover:bg-orange-900",
      loadingTableClass: "text-orange-900",
      loadingTableBackgroundClass: "bg-[radial-gradient(circle_at_top_right,_#fff8f0,_#fff3e6,_#fff9f3)]",
    },
    "yellow": {
      selectThemeButtonClasses: "border-yellow-700 bg-yellow-400 hover:bg-yellow-600",
      backToLeaguesButtonClasses: "border-yellow-700 bg-yellow-900 hover:bg-yellow-950",
      displayedComponentTitleBoldClass: "text-yellow-500",
      selectedTabButtonClasses: "border-yellow-600 bg-yellow-500 hover:bg-yellow-600",

      headerTableClass: "bg-yellow-200 hover:bg-yellow-300",
      bodyTableClass: "bg-yellow-50",   
      headerNameClass: "bg-yellow-800 hover:bg-yellow-900",
      loadingTableClass: "text-yellow-900",
      loadingTableBackgroundClass: "bg-[radial-gradient(circle_at_top_right,_#fffeef,_#fffce6,_#fffef3)]",
    },
    "green": {
      selectThemeButtonClasses: "border-green-700 bg-green-500 hover:bg-green-700",
      backToLeaguesButtonClasses: "border-green-700 bg-green-900 hover:bg-green-950",
      displayedComponentTitleBoldClass: "text-green-600",
      selectedTabButtonClasses: "border-green-700 bg-green-600 hover:bg-green-700",

      headerTableClass: "bg-green-200 hover:bg-green-300",
      bodyTableClass: "bg-green-50",   
      headerNameClass: "bg-green-800 hover:bg-green-900",
      loadingTableClass: "text-green-900",
      loadingTableBackgroundClass: "bg-[radial-gradient(circle_at_top_right,_#f2fff8,_#eafff2,_#f4fff9)]",
    },
    "blue": {
      selectThemeButtonClasses: "border-blue-700 bg-blue-600 hover:bg-blue-800",
      backToLeaguesButtonClasses: "border-blue-700 bg-blue-900 hover:bg-blue-950",
      displayedComponentTitleBoldClass: "text-blue-700",
      selectedTabButtonClasses: "border-blue-700 bg-blue-600 hover:bg-blue-700",

      headerTableClass: "bg-blue-200 hover:bg-blue-300",
      bodyTableClass: "bg-blue-50",   
      headerNameClass: "bg-blue-800 hover:bg-blue-900",
      loadingTableClass: "text-blue-900",
      loadingTableBackgroundClass: "bg-[radial-gradient(circle_at_top_right,_#f0f7ff,_#e4f0ff,_#ebf5ff)]",
    },
    "violet": {
      selectThemeButtonClasses: "border-violet-700 bg-violet-500 hover:bg-violet-700",
      backToLeaguesButtonClasses: "border-violet-700 bg-violet-900 hover:bg-violet-950",
      displayedComponentTitleBoldClass: "text-violet-700",
      selectedTabButtonClasses: "border-violet-700 bg-violet-600 hover:bg-violet-700",

      headerTableClass: "bg-violet-200 hover:bg-violet-300",
      bodyTableClass: "bg-violet-50",   
      headerNameClass: "bg-violet-800 hover:bg-violet-900",
      loadingTableClass: "text-violet-900",
      loadingTableBackgroundClass: "bg-[radial-gradient(circle_at_top_right,_#faf3ff,_#f5eaff,_#fbf5ff)]",
    },
  } as const;

  reset(){
    this.statsTheme.set("blue");
  }
}
