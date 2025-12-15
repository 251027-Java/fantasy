import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, HostListener } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NgIcon } from '@ng-icons/core';
import { BrnSelectImports } from '@spartan-ng/brain/select';
import {
	HlmAccordion,
	HlmAccordionContent,
	HlmAccordionIcon,
	HlmAccordionImports,
	HlmAccordionItem,
} from '@spartan-ng/helm/accordion';
import { HlmButton, HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmCardDescription } from '@spartan-ng/helm/card';
import { HlmFormFieldImports } from '@spartan-ng/helm/form-field';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { HlmInput } from '@spartan-ng/helm/input';
import { HlmSelectImports } from '@spartan-ng/helm/select';
import { environment } from '../../../environments/environment';
import { AuthRequest, AuthResponse } from '../../interface/auth-interfaces';
import { AuthService } from '../../services/auth-service';
import { LoginService } from '../../services/login-service';
import { Navbar } from '../navbar/navbar';

declare var google: any;

@Component({
	selector: 'app-google-auth',
	standalone: true,
	imports: [
		Navbar,
		HlmFormFieldImports,
		HlmSelectImports,
		BrnSelectImports,
		HlmButtonImports,
		CommonModule,
		ReactiveFormsModule,
		Navbar,
		NgIcon,
		HlmIconImports,
		HlmAccordionImports,
		HlmCardDescription,
		HlmAccordionIcon,
		HlmAccordionContent,
		HlmAccordionItem,
		HlmAccordion,
	],
	templateUrl: './google-auth.html',
	styleUrl: './google-auth.css',
})
export class GoogleAuth implements AfterViewInit {
	section1Visible = false;
	section2Visible = false;
	section3Visible = false;

	constructor(
		private router: Router,
		private authService: AuthService,
		private loginServe: LoginService,
	) {}

	ngOnInit() {
		setTimeout(() => this.checkVisibility(), 100);
	}

	loadGoogleClientId(): string {
		// grab the google client id from the environment variables
		return environment.googleClientId;
	}

	// TODO: add types
	private codeClient: any;

	ngAfterViewInit(): void {
		// sets up google client to request a code for the backend to exchange for a token
		this.checkAndInitializeGoogle();
		this.renderManualButton();
	}

	private checkAndInitializeGoogle(): void {
		if (typeof google !== 'undefined') {
			this.initializeCodeClient();
		} else {
			console.error('Google Identity Services script not loaded, retrying...');
			setTimeout(() => this.checkAndInitializeGoogle(), 100);
		}
	}

	private initializeCodeClient(): void {
		this.codeClient = google.accounts.oauth2.initCodeClient({
			client_id: this.loadGoogleClientId(),
			scope: 'openid email profile', // Scopes required for user info
			redirect_uri: window.location.origin,
			ux_mode: 'popup', // Use popup mode for SPA
			callback: (response: AuthRequest) => this.handleCodeResponse(response),
		});
	}

	signInWithGoogle() {
		if (this.codeClient) {
			this.codeClient.requestCode();
		}
	}

	// Example of a manual button rendering function
	private renderManualButton() {
		// get button element and unhide it
		const button = document.getElementById('google-btn');
		button?.classList.remove('hidden');
	}

	// handleCredentialResponse(response: any) {
	//   console.log("Encoded JWT ID token: " + response.code);
	//   this.authService.verifyGoogleToken(response.code).subscribe({
	//     next: (resp: any) => {
	//       console.log("Auth success", resp);
	//       this.ngZone.run(() => {
	//         this.router.navigateByUrl('');
	//       });
	//     },
	//     error: (err: any) => console.error("Auth failed", err)
	//   });
	// }

	// the above but actually verfying it comes from our backend
	private handleCodeResponse(response: AuthRequest) {
		if (response.code) {
			console.log(`Authorization·Code·received:·${response.code}`);

			this.authService.verifyGoogleCode(response.code).subscribe({
				next: (resp: AuthResponse) => {
					sessionStorage.setItem('token', resp.jwtToken);

					console.log(`Code exchange successful: ${resp}`);
					console.log(`token: ${sessionStorage.getItem('token')}`);
					// redirect to the login page
					this.loginServe.Login();
					this.router.navigateByUrl('league');
				},
				error: (err: any) => {
					console.error('Code exchange failed', err);
				},
			});
		} else {
			console.error('Authorization failed, no code received.', response);
		}
	}

	// Create functionality for components loading into window
	@HostListener('window:scroll', [])
	onScroll() {
		this.checkVisibility();
	}

	checkVisibility() {
		const sections = [
			{ id: 'section1', property: 'section1Visible' },
			{ id: 'section1', property: 'section1Visible' },
			{ id: 'section1', property: 'section1Visible' },
		];

		sections.forEach((section) => {
			const element = document.getElementById(section.id);
			if (element) {
				const rect = element.getBoundingClientRect();
				const isVisible = rect.top < window.innerHeight * 0.75;
				(this as any)[section.property] = isVisible;
			}
		});
	}
}
