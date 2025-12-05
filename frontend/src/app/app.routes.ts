import type { Routes } from '@angular/router';
import { Login } from './components/login/login';
import { League } from './components/league/league';

export const routes: Routes = [
    {
        path: '',
        component:Login
    },
    {
        path: "league",
        component:League
    }
];
