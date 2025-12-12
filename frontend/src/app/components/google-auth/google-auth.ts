import { AfterViewInit, Component } from '@angular/core';
import { Router } from '@angular/router';
import { HlmButton } from '@spartan-ng/helm/button';
import { environment } from '../../../environments/environment';
import { AuthRequest, AuthResponse } from '../../interface/auth-interfaces';
import { AuthService } from '../../services/auth-service';

declare var google: any;

@Component({
	selector: 'app-google-auth',
	standalone: true,
	imports: [],
	templateUrl: './google-auth.html',
	styleUrl: './google-auth.css',
})
export class GoogleAuth implements AfterViewInit {
	constructor(
		private router: Router,
		private authService: AuthService,
	) {}

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
					this.router.navigateByUrl('login');
				},
				error: (err: any) => {
					console.error('Code exchange failed', err);
				},
			});
		} else {
			console.error('Authorization failed, no code received.', response);
		}
	}
}
