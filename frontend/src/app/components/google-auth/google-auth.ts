import { Component, AfterViewInit, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login-service';
import { AuthService } from '../../services/auth-service';
import { environment } from '../../../environments/environment';
import { AuthRequest, AuthResponse } from '../../interface/auth-interfaces';

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
        redirect_uri: 'http://localhost:4200',
        ux_mode: 'popup',             // Use popup mode for SPA
        callback: (response: AuthRequest) => this.handleCodeResponse(response)
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
    // get button element and unhide it 
    const button = document.getElementById("google-btn");
    button?.classList.remove("hidden");

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
  handleCodeResponse(response: AuthRequest) {
    // Check for the code property
    if (response.code) {
      console.log("Authorization Code received: " + response.code);

      // Send the code to your backend service for the secure exchange
      this.authService.verifyGoogleCode(response.code).subscribe({
        next: (resp: AuthResponse) => {
          // ... success logic
          // TODO: give the user a token
          sessionStorage.setItem('token', resp.jwtToken);


          console.log(resp, "Code exchange successful");
          console.log('token', sessionStorage.getItem('token'));
          // redirect to the login page
          this.router.navigateByUrl('login');
        },
        error: (err: any) => {
          console.error("Code exchange failed", err)
        }
      });
    } else {
      console.error("Authorization failed, no code received.", response);
    }
  }
}
