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
            selectThemeButtonClasses: 'border-red-600 bg-red-800 hover:bg-red-700',
            backToLeaguesButtonClasses: 'border-red-700 bg-red-900 hover:bg-red-800',
            displayedComponentTitleBoldClass: 'text-red-500',
            selectedTabButtonClasses: 'border-red-600 bg-red-800 hover:bg-red-700',
            headerTableClass: 'bg-red-900 hover:bg-red-800',
            bodyTableClass: 'bg-black/20', 
            headerNameClass: 'bg-red-950 hover:bg-red-900',
            loadingTableClass: 'text-red-100',
			tableBodyGradientClass:
                'bg-[radial-gradient(circle_at_top_right,_#3a0d0f,_#521418,_#2a0a0c)]',
            loadingTableBackgroundClass:
                'bg-[radial-gradient(circle_at_top_right,_#3a0d0f,_#521418,_#2a0a0c)]', 
        },
        orange: {
            selectThemeButtonClasses:
                'border-orange-600 bg-orange-800 hover:bg-orange-700',
            backToLeaguesButtonClasses:
                'border-orange-700 bg-orange-900 hover:bg-orange-800',
            displayedComponentTitleBoldClass: 'text-orange-500',
            selectedTabButtonClasses:
                'border-orange-600 bg-orange-800 hover:bg-orange-700',

            headerTableClass: 'bg-orange-900 hover:bg-orange-800',
            bodyTableClass: 'bg-black/20', 
            headerNameClass: 'bg-orange-950 hover:bg-orange-900',
            loadingTableClass: 'text-orange-100',
            loadingTableBackgroundClass:
                'bg-[radial-gradient(circle_at_top_right,_#3b1d06,_#5c2a08,_#2a1304)]', 
				tableBodyGradientClass:
                'bg-[radial-gradient(circle_at_top_right,_#3b1d06,_#5c2a08,_#2a1304)]',
        },
        yellow: {
            selectThemeButtonClasses:
                'border-yellow-600 bg-yellow-700 hover:bg-yellow-600',
            backToLeaguesButtonClasses:
                'border-yellow-700 bg-yellow-900 hover:bg-yellow-800',
            displayedComponentTitleBoldClass: 'text-yellow-400',
            selectedTabButtonClasses:
                'border-yellow-600 bg-yellow-700 hover:bg-yellow-600',

            headerTableClass: 'bg-yellow-900 hover:bg-yellow-800',
            bodyTableClass: 'bg-black/20', 
            headerNameClass: 'bg-yellow-950 hover:bg-yellow-900',
            loadingTableClass: 'text-yellow-100',
            loadingTableBackgroundClass:
                'bg-[radial-gradient(circle_at_top_right,_#2f2a05,_#4a4107,_#1e1b04)]',
				tableBodyGradientClass:
                'bg-[radial-gradient(circle_at_top_right,_#2f2a05,_#4a4107,_#1e1b04)]',
        },
        green: {
            selectThemeButtonClasses:
                'border-green-600 bg-green-800 hover:bg-green-700',
            backToLeaguesButtonClasses:
                'border-green-700 bg-green-900 hover:bg-green-800',
            displayedComponentTitleBoldClass: 'text-green-500',
            selectedTabButtonClasses:
                'bg-[radial-gradient(circle_at_top_right,_#072c1a,_#0e4227,_#051b11)]',

            headerTableClass: 'bg-green-900 hover:bg-green-800',
            bodyTableClass: 'bg-black/20', 
            headerNameClass: 'bg-green-950 hover:bg-green-900',
            loadingTableClass: 'text-green-100',
            loadingTableBackgroundClass:
                'bg-[radial-gradient(circle_at_top_right,_#072c1a,_#0e4227,_#051b11)]', 
				tableBodyGradientClass:
                'bg-[radial-gradient(circle_at_top_right,_#072c1a,_#0e4227,_#051b11)]',
        },
        blue: {
            selectThemeButtonClasses: 'border-indigo-600 bg-indigo-800 hover:bg-indigo-700',
            backToLeaguesButtonClasses:
                'border-indigo-700 bg-indigo-900 hover:bg-indigo-800',
            displayedComponentTitleBoldClass: 'text-indigo-500',
            selectedTabButtonClasses: 'border-indigo-600 bg-indigo-800 hover:bg-indigo-700',

            headerTableClass: 'bg-indigo-900 hover:bg-indigo-800',
            bodyTableClass: 'bg-black/20', 
            headerNameClass: 'bg-indigo-950 hover:bg-indigo-900',
            loadingTableClass: 'text-indigo-100',
            loadingTableBackgroundClass:
                'bg-[radial-gradient(circle_at_top_right,_#0e0a29,_#1c1440,_#0a081e)]',
				tableBodyGradientClass:
                'bg-[radial-gradient(circle_at_top_right,_#0e0a29,_#1c1440,_#0a081e)]',
        },
        violet: {
            selectThemeButtonClasses:
                'border-purple-600 bg-purple-800 hover:bg-purple-700',
            backToLeaguesButtonClasses:
                'border-purple-700 bg-purple-900 hover:bg-purple-800',
            displayedComponentTitleBoldClass: 'text-purple-500',
            selectedTabButtonClasses:
                'border-purple-600 bg-purple-800 hover:bg-purple-700',

            headerTableClass: 'bg-purple-900 hover:bg-purple-800',
            bodyTableClass: 'bg-black/20',
            headerNameClass: 'bg-purple-950 hover:bg-purple-900',
            loadingTableClass: 'text-purple-100',
            loadingTableBackgroundClass:
                'bg-[radial-gradient(circle_at_top_right,_#1f0a2d,_#351151,_#170724)]', 
				tableBodyGradientClass:
                'bg-[radial-gradient(circle_at_top_right,_#1f0a2d,_#351151,_#170724)]',
        },
    } as const;

    reset() {
        this.statsTheme.set(this.initialTheme);
    }
}