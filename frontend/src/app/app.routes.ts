import type { Routes } from '@angular/router';
import { League } from './components/league/league';
import { Login } from './components/login/login';
import { StatsPage } from './components/stats-page/stats-page';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
	{
		path: '',
		component: Login,
	},
	{
		path: 'league',
		component: League,
		canActivate: [authGuard],
	},
	{
		path: 'stats',
		component: StatsPage,
		canActivate: [authGuard],
	},
];
