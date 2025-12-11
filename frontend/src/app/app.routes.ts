import type { Routes } from '@angular/router';
import { League } from './components/league/league';
import { Login } from './components/login/login';
import { StatsPage } from './components/stats-page/stats-page';
import { AuthGuard } from './guards/auth-guard-guard';
import { GoogleAuth } from './components/google-auth/google-auth';

export const routes: Routes = [
	{
		path: '',
		redirectTo: 'auth',
		pathMatch: 'full'
	},
	{
		path: 'auth',
		component: GoogleAuth,
	},
	{
		path: 'login',
		component: Login,
		canActivate: [AuthGuard]
	},
	{
		path: 'league',
		component: League,
		canActivate: [AuthGuard]
	},
	{
		path: 'stats',
		component: StatsPage,
		canActivate: [AuthGuard]
	},
	{
		path: '**',
		redirectTo: 'auth',
		pathMatch: 'full'
	}
];
