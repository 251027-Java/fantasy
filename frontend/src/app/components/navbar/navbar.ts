import { ViewportScroller } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth-service';
import { LoginService } from '../../services/login-service';
import { StatsService } from '../../services/stats-service';
import { ThemeService } from '../../services/theme-service';

@Component({
	selector: 'app-navbar',
	imports: [],
	templateUrl: './navbar.html',
	styleUrl: './navbar.css',
})
export class Navbar {
	constructor(
		public loginServe: LoginService,
		public authServe: AuthService,
		private statsService: StatsService,
		private themeService: ThemeService,
		private router: Router,
		private scroller: ViewportScroller,
	) {}

	Logout(): void {
		this.loginServe.logout();
		this.statsService.reset();
		this.themeService.reset();
		this.router.navigateByUrl('');
	}

	goHome(): void {
		this.router.navigateByUrl('auth');
	}

	goSearch(): void {
		this.router.navigateByUrl('league');
	}

	scrollToLogin(): void {
		// document.getElementById('Login')?.scrollIntoView({ behavior: 'smooth' });
		this.scroller.scrollToAnchor('Login');
	}
}
