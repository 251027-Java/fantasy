import { Component, OnInit } from '@angular/core';
import {
	FormControl,
	FormGroup,
	ReactiveFormsModule,
	Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { BrnSelectImports } from '@spartan-ng/brain/select';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmFormFieldImports } from '@spartan-ng/helm/form-field';
import { HlmInput } from '@spartan-ng/helm/input';
import { HlmSelectImports } from '@spartan-ng/helm/select';
import { LoginResponse } from '../../interface/login-response';
import { LoginService } from '../../services/login-service';
import { StatsService } from '../../services/stats-service';

@Component({
	selector: 'app-login',
	imports: [
		HlmFormFieldImports,
		HlmSelectImports,
		HlmInput,
		HlmSelectImports,
		BrnSelectImports,
		HlmButtonImports,
		ReactiveFormsModule,
	],
	templateUrl: './login.html',
	styleUrl: './login.css',
})
export class Login implements OnInit {
	public loginControl = new FormGroup({
		user: new FormControl('', [Validators.required]),
	});
	//new FormControl('', [Validators.required, Validators.email]);
	constructor(
		private router: Router,
		private loginServe: LoginService,
	) {}

	ngOnInit(): void {
		this.loginControl.valueChanges.subscribe((value) => {
			console.log('Login input changed to:', value.user);
		});
	}

	Login(): void {
		if (this.loginControl.valid) {
			// Perform login logic here

			this.loginServe.usernameSet(this.loginControl.value.user ?? '');

			// Perform a check to see if the user exists in the backend
			console.log('Logging in with:', this.loginControl.value.user);
			//this.loginServe.loginUser(this.loginControl.value.user!);
			this.loginServe.getLeagues().subscribe({
				next: (response) => {
					this.loginServe.LeagueResponse.set(response);
					console.log('Login successful');
					console.log(
						'These are the leagues: ' + JSON.stringify(response.leagues),
					);
					this.router.navigateByUrl('league');
				},
				error: (err: string) => {
					console.log('Login failed: ' + err);
				},
			});
		} else {
			console.log('Login form is invalid');
		}
	}
}
