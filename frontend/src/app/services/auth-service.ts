import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthResponse } from '../interface/auth-response';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  constructor(private http: HttpClient) { }

  // verifyGoogleToken(token: string): Observable<AuthResponse> {
  //   return this.http.post<AuthResponse>('api/auth/google', { token });
  // }

  verifyGoogleCode(code: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('api/auth/google', { code });
  }
}
