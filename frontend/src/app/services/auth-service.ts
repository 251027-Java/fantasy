import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import jwt_decode from 'jwt-decode';
import { Observable } from 'rxjs';
import { AuthResponse } from '../interface/auth-interfaces';
import { Router } from '@angular/router';

@Injectable({
	providedIn: 'root',
})
export class AuthService {

	constructor(private http: HttpClient, private router: Router) {}

	// verifyGoogleToken(token: string): Observable<AuthResponse> {
	//   return this.http.post<AuthResponse>('api/auth/google', { token });
	// }

	verifyGoogleCode(code: string): Observable<AuthResponse> {
		return this.http.post<AuthResponse>('api/auth/google', { code });
	}

	isAuthorized(): boolean {
		// check if the token exists
		const token: string | null = sessionStorage.getItem('token');
		if (token) {
			// send api request to validate token
			try {
				// TODO: fix this type
				const decoded: any = jwt_decode(token);
				const isExpired = decoded.exp * 1000 < Date.now();

				console.log(decoded.exp);
				console.log(Date.now());

				return !isExpired;
			} catch (_error) {
				return false; // Token is malformed
			}
		}
		return false;
	}

	getToken(): string {
		const token: string | null = sessionStorage.getItem('token');
		return token ? token : 'no_token_found';
	}

	logout(): void {
		// remove the token from the session storage
		sessionStorage.removeItem('token');
		// redirect to auth page
		this.router.navigateByUrl('/auth');
	}
}
