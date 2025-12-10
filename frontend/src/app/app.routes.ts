import type { Routes } from '@angular/router';
import { League } from './components/league/league';
import { Login } from './components/login/login';
import { StatsPage } from './components/stats-page/stats-page';

import { GoogleAuth } from './components/google-auth/google-auth';

export const routes: Routes = [
	{
		path: 'auth',
		component: GoogleAuth,
	},
	{
		path: '',
		component: Login,
	},
	{
		path: 'league',
		component: League,
	},
	{
		path: 'stats',
		component: StatsPage,
	},
];
