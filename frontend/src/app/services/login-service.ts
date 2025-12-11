import { HttpClient } from '@angular/common/http';
import { Injectable, signal, WritableSignal } from '@angular/core';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { LoginResponse } from '../interface/login-response';
import { AuthService } from './auth-service';

@Injectable({
	providedIn: 'root',
})
export class LoginService {
	private username: string = 'leeeem';

	// Inject HttpClient for making HTTP requests
	constructor(
		private http: HttpClient,
		private authService: AuthService,
	) {}

	LeagueResponse: WritableSignal<LoginResponse> = signal({
		userId: '',
		leagues: [],
	});

	// Returns the list of leagues from the LoginDTO endpoint: (endpoint found in mainController in backend)
	getLeagues(): Observable<LoginResponse> {
		// Gain Login response
		try {
			// add the Bearer token to the request
			let resp: Observable<LoginResponse> = this.http.get<LoginResponse>(
				`api/login/${this.username}`,
				{
					headers: {
						Authorization: `Bearer ${this.authService.getToken()}`,
					},
				},
			);

			resp = resp.pipe(
				map<any, LoginResponse>((data) => {
					// Map the received data to the Leagues interface
					const processedResp: LoginResponse = { userId: '', leagues: [] };
					for (const league of data.leagues) {
						// Push each league into the leagues array with proper formatting
						processedResp.leagues.push({
							id: league.leagueId,
							name: league.leagueName,
						});
					}
					//console.log("Raw data1 (JSON string):", JSON.stringify(processedResp, null, 2));

					return processedResp;
				}),
			);

			return resp;
		} catch (_InvalidUsernameException) {
			console.log('Invalid Username Exception caught in LoginService');
		}

		throw new Error('Error in getting leagues in LoginService');
	}

	usernameSet(username: string) {
		this.username = username;
	}

	// It is initialized to 'false' (logged out)
	// BehaviorSubject is used to hold the current authentication state
	// Allows components to subscribe and get the latest value (fast)
	private loggedInSubject: BehaviorSubject<boolean> =
		new BehaviorSubject<boolean>(false);

	public isLoggedIn$: Observable<boolean> = this.loggedInSubject.asObservable();

	Login(): void {
		if (!this.loggedInSubject.value) {
			this.loggedInSubject.next(true);
			console.log('login: State is now TRUE');
		}
	}

	logout(): void {
		if (this.loggedInSubject.value) {
			this.loggedInSubject.next(false);
			console.log('Logut: State is now FALSE');
		}
	}

	getIsLoggedIn(): boolean {
		return this.loggedInSubject.value;
	}
}
