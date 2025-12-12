import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { BrnSelectImports } from '@spartan-ng/brain/select';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmFormFieldImports } from '@spartan-ng/helm/form-field';
import { HlmInput } from '@spartan-ng/helm/input';
import { HlmSelectImports } from '@spartan-ng/helm/select';
import { CardData } from '../../interface/card-data';
import { AuthService } from '../../services/auth-service';
import { LoginService } from '../../services/login-service';
import { StatsService } from '../../services/stats-service';
import { CommonModule } from '@angular/common';
import { ThemeService } from '../../services/theme-service';

@Component({
	selector: 'app-league',
	imports: [
		HlmFormFieldImports,
		HlmSelectImports,
		HlmSelectImports,
		BrnSelectImports,
		HlmButtonImports,
		CommonModule,
		ReactiveFormsModule,
		HlmInput,
	],
	templateUrl: './league.html',
	styleUrl: './league.css',
})
export class League implements OnInit {
	// Determines number of items you'll see wtihin carousel
	constructor(
		private router: Router,
		private loginServe: LoginService,
		private statsService: StatsService,
		private authService: AuthService,
		private themeService: ThemeService,
		private cdRef: ChangeDetectorRef,
	) {}

	public cardList: CardData[] = [];
	public loading: boolean = true;
	public error: boolean = false;
	public userSearchList: String[] = [];
	public emptyListUser: boolean = false;
	private currentCard: CardData = {id: "", title: "", users: [], description: "", buttonText: ""};

	ngOnInit(): void {

		this.cardList = this.loginServe.cardList;

		this.loading = false;
		this.error = false;

		if(this.loginServe.cardList.length == 0){
			this.emptyListUser = true;
		} else {this.emptyListUser = false;}

		this.cdRef.detectChanges();

		console.log("current username: " + this.loginServe.username)

		//console.log("Raw data3 (JSON string):", JSON.stringify(this.cardList, null, 2));
	}

	Logout(): void {
		this.loginServe.logout();
		this.statsService.reset();
		this.themeService.reset();
		this.router.navigateByUrl('');
	}

	viewLeague(card: CardData): void {
		console.log('Viewing league with id:', card.id);
		this.statsService.setLeague(String(card.id), card.title);
		this.router.navigateByUrl('stats');
	}


	public searchControl = new FormGroup({
		user: new FormControl('', [Validators.required]),
	});
	//new FormControl('', [Validators.required, Validators.email]);

	removeFromUserList(card: CardData): void{

		this.cdRef.detectChanges();
	}

	addUserNameToSearchList(name: string): void {

	}

	retrieveLeagues(): void {

		// this.cardList = [];
		if (this.searchControl.valid) {
			// Perform login logic here

			// Check if there's been a change
			this.error = false;
			this.loading = true;
			this.cdRef.detectChanges()

			//console.log("current username " + this.searchControl.value.user)
			this.loginServe.usernameSet(this.searchControl.value.user ?? '');

			// Perform a check to see if the user exists in the backend
			//console.log('Logging in with:', this.searchControl.value.user);
			//this.loginServe.loginUser(this.loginControl.value.user!);

			this.loginServe.getLeagues().subscribe({
				next: (response) => {
					
					//console.log('search successful');
					// console.log(
					// 	`These are the leagues: ${JSON.stringify(response.leagues)}`,
					// );

					// this.cardList = response.leagues.map((league) => ({
					// 	id: league.id,
					// 	title: league.name,
					// 	description: `Welcome to the ${league.name} league!`,
					// 	buttonText: 'View'
					// }));

					response.leagues.forEach((league) => {

						
						this.currentCard = {
							id: league.id, 
							title: league.name, 
							users: [], 
							description: `Welcome to the ${league.name} league!`, 
							buttonText: 'View'}

						// console.log("this is working for league: " + this.currentCard.id);
						this.loginServe.addToUserList(this.currentCard, this.searchControl.value.user ?? '');

						
					});
					
					this.loading = false;

					if(this.loginServe.cardList.length == 0){
						this.emptyListUser = true;
					}else{
						this.emptyListUser = false;
					}

					this.cdRef.detectChanges();

				},
				error: (err: string) => {
					this.loading = false;
					this.error = true;
					console.log(`Retrieval of Leagues failed: ${err}`);
					this.cdRef.detectChanges();
				},

			});
		} else {
			console.log('Login form is invalid');
		}
	}
}
