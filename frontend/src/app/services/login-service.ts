import { HttpClient } from '@angular/common/http';
import { Injectable, signal, WritableSignal } from '@angular/core';
import { map, Observable } from 'rxjs';
import { LoginResponse } from '../interface/login-response';

@Injectable({
	providedIn: 'root',
})
export class LoginService {
	private username: string = 'leeeem';

	// Inject HttpClient for making HTTP requests
	constructor(private http: HttpClient) {}

	LeagueResponse: WritableSignal<LoginResponse> = signal({
		userId: '',
		leagues: [],
	});

	// Returns the list of leagues from the LoginDTO endpoint: (endpoint found in mainController in backend)
	getLeagues(): Observable<LoginResponse> {
		// Gain Login response
		try {
			let resp: Observable<LoginResponse> = this.http.get<LoginResponse>(
				`api/login/${this.username}`,
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
}
