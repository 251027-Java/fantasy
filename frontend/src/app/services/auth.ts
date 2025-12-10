import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  // It is initialized to 'false' (logged out)
  // BehaviorSubject is used to hold the current authentication state
  // Allows components to subscribe and get the latest value (fast)
  private loggedInSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  
  // 2. Public Observable for components to subscribe to and react to state changes
  public isLoggedIn$: Observable<boolean> = this.loggedInSubject.asObservable();

  constructor() {
    
    console.log('AuthService initialized. Logged in:', this.loggedInSubject.value);
  }


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