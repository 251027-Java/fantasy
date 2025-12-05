export interface Theme {
    selectThemeButtonClasses: string;
    backToLeaguesButtonClasses: string;
    displayedComponentTitleBoldClass: string;
    selectedTabButtonClasses: string;

    headerTableClass: string;
    bodyTableClass: string;    
    headerNameClass: string;
    loadingTableClass: string;
    loadingTableBackgroundClass: string;
}

export type ThemeColor = "red" | "orange" | "yellow" | "green" | "blue" | "violet"