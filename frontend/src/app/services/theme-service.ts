import { Injectable, signal, WritableSignal } from '@angular/core';
import { Theme, ThemeColor } from '../interface/theme';

@Injectable({
	providedIn: 'root',
})
export class ThemeService {
	readonly initialTheme: ThemeColor = 'red';

	statsTheme: WritableSignal<ThemeColor> = signal(this.initialTheme);

	getThemeColors(): ThemeColor[] {
		return this.colorOrder;
	}
	getThemeClassString(themeColor: ThemeColor, themeClass: keyof Theme): string {
		return this.statsThemes[themeColor][themeClass];
	}
	getCurrentThemeClassString(themeClass: keyof Theme): string {
		return this.statsThemes[this.statsTheme()][themeClass];
	}

	setStatsTheme(themeColor: ThemeColor): void {
		this.statsTheme.set(themeColor);
	}

	private readonly colorOrder: ThemeColor[] = [
		'red',
		'orange',
		'yellow',
		'green',
		'blue',
		'violet',
	] as const;
	private readonly statsThemes: Record<ThemeColor, Theme> = {
		red: {
			selectThemeButtonClasses: 'border-red-400 bg-red-500 hover:bg-red-400',
			backToLeaguesButtonClasses: 'border-red-700 bg-red-900 hover:bg-red-800',
			displayedComponentTitleBoldClass: 'text-red-600',
			selectedTabButtonClasses: 'border-red-400 bg-red-600 hover:bg-red-400',

			headerTableClass: 'bg-red-800 hover:bg-red-700',
			bodyTableClass: 'bg-red-500',
			headerNameClass: 'bg-red-950 hover:bg-red-900',
			loadingTableClass: 'text-red-100',
			loadingTableBackgroundClass:
				'bg-[radial-gradient(circle_at_top_right,_#3a0d0f,_#521418,_#2a0a0c)]',
		},
		orange: {
			selectThemeButtonClasses:
				'border-orange-400 bg-orange-500 hover:bg-orange-300',
			backToLeaguesButtonClasses:
				'border-orange-700 bg-orange-900 hover:bg-orange-800',
			displayedComponentTitleBoldClass: 'text-orange-600',
			selectedTabButtonClasses:
				'border-orange-400 bg-orange-600 hover:bg-orange-400',

			headerTableClass: 'bg-orange-800 hover:bg-orange-700',
			bodyTableClass: 'bg-orange-500',
			headerNameClass: 'bg-orange-950 hover:bg-orange-900',
			loadingTableClass: 'text-orange-100',
			loadingTableBackgroundClass:
				'bg-[radial-gradient(circle_at_top_right,_#3b1d06,_#5c2a08,_#2a1304)]',
		},
		yellow: {
			selectThemeButtonClasses:
				'border-yellow-300 bg-yellow-400 hover:bg-yellow-200',
			backToLeaguesButtonClasses:
				'border-yellow-700 bg-yellow-900 hover:bg-yellow-800',
			displayedComponentTitleBoldClass: 'text-yellow-500',
			selectedTabButtonClasses:
				'border-yellow-300 bg-yellow-500 hover:bg-yellow-300',

			headerTableClass: 'bg-yellow-800 hover:bg-yellow-700',
			bodyTableClass: 'bg-[#E4AB07]',
			headerNameClass: 'bg-yellow-950 hover:bg-yellow-900',
			loadingTableClass: 'text-yellow-100',
			loadingTableBackgroundClass:
				'bg-[radial-gradient(circle_at_top_right,_#2f2a05,_#4a4107,_#1e1b04)]',
		},
		green: {
			selectThemeButtonClasses:
				'border-green-400 bg-green-500 hover:bg-green-400',
			backToLeaguesButtonClasses:
				'border-green-700 bg-green-900 hover:bg-green-800',
			displayedComponentTitleBoldClass: 'text-green-600',
			selectedTabButtonClasses:
				'border-green-400 bg-green-600 hover:bg-green-400',

			headerTableClass: 'bg-green-800 hover:bg-green-700',
			bodyTableClass: 'bg-green-600',
			headerNameClass: 'bg-green-950 hover:bg-green-900',
			loadingTableClass: 'text-green-100',
			loadingTableBackgroundClass:
				'bg-[radial-gradient(circle_at_top_right,_#072c1a,_#0e4227,_#051b11)]',
		},
		blue: {
			selectThemeButtonClasses: 'border-blue-500 bg-blue-600 hover:bg-blue-400',
			backToLeaguesButtonClasses:
				'border-blue-600 bg-blue-900 hover:bg-blue-700',
			displayedComponentTitleBoldClass: 'text-blue-700',
			selectedTabButtonClasses: 'border-blue-400 bg-blue-600 hover:bg-blue-400',

			headerTableClass: 'bg-blue-800 hover:bg-blue-700',
			bodyTableClass: 'bg-[#4E94F8]',
			headerNameClass: 'bg-blue-950 hover:bg-blue-900',
			loadingTableClass: 'text-blue-100',
			loadingTableBackgroundClass:
				'bg-[radial-gradient(circle_at_top_right,_#0a1c33,_#112b4a,_#061423)]',
		},
		violet: {
			selectThemeButtonClasses:
				'border-violet-400 bg-violet-500 hover:bg-violet-400',
			backToLeaguesButtonClasses:
				'border-violet-600 bg-violet-900 hover:bg-violet-700',
			displayedComponentTitleBoldClass: 'text-violet-700',
			selectedTabButtonClasses:
				'border-violet-400 bg-violet-600 hover:bg-violet-400',

			headerTableClass: 'bg-violet-800 hover:bg-violet-700',
			bodyTableClass: 'bg-[#B46DFA]',
			headerNameClass: 'bg-violet-950 hover:bg-violet-900',
			loadingTableClass: 'text-violet-100',
			loadingTableBackgroundClass:
				'bg-[radial-gradient(circle_at_top_right,_#1d0b35,_#2d114e,_#150726)]',
		},
	} as const;

	reset() {
		this.statsTheme.set(this.initialTheme);
	}
}
