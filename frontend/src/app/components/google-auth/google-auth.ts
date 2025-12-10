import { Component, AfterViewInit, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login-service';
import { AuthService } from '../../services/auth-service';
import { environment } from '../../../environments/environment';

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
    private ngZone: NgZone
  ) { }

  loadGoogleClientId(): String {
    // grab the google client id from the environment variables
    return environment.googleClientId;
  }

  // TODO: add types 
  private codeClient: any;


  ngAfterViewInit(): void {
    // sets up google client to request a code for the backend to exchange for a token
    if (typeof google !== 'undefined') {
      this.codeClient = google.accounts.oauth2.initCodeClient({
        client_id: this.loadGoogleClientId(),
        scope: 'openid email profile', // Scopes required for user info
        redirect_uri: 'http://localhost:4200/auth',
        ux_mode: 'popup',             // Use popup mode for SPA
        callback: (response: any) => this.handleCodeResponse(response) 
      });
      // need to render the button after the client is initialized
      // so that functionality is available on press
      this.renderManualButton();
    } else {
      console.error('Google Identity Services script not loaded');
    }
  }

  signInWithGoogle() {
    if (this.codeClient) {
      this.codeClient.requestCode(); // This opens the Google consent popup
    }
  }

  // Example of a manual button rendering function
  renderManualButton() {
    const button = document.createElement('button');
    button.id = "google-btn";
    button.textContent = "Sign in with Google";
    button.onclick = () => this.signInWithGoogle();
    document.getElementById("google-btn-container")?.appendChild(button);
  }

  handleCredentialResponse(response: any) {
    console.log("Encoded JWT ID token: " + response.code);
    this.authService.verifyGoogleToken(response.code).subscribe({
      next: (resp: any) => {
        console.log("Auth success", resp);
        this.ngZone.run(() => {
          this.router.navigateByUrl('');
        });
      },
      error: (err: any) => console.error("Auth failed", err)
    });
  }

  // NOTE: Renamed from handleCredentialResponse to reflect receiving a Code
  handleCodeResponse(response: any) {
    // Check for the code property
    if (response.code) {
      console.log("Authorization Code received: " + response.code);
      
      // Send the code to your backend service for the secure exchange
      this.authService.verifyGoogleCode(response.code).subscribe({
        next: (resp: any) => {
          // ... success logic
        },
        error: (err: any) => console.error("Code exchange failed", err)
      });
    } else {
      console.error("Authorization failed, no code received.", response);
    }
    this.ngZone.run(() => {
      this.router.navigateByUrl('');
    });
  }
}
