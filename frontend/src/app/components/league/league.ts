import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
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
import { CardData } from '../../interface/card-data';
import { AuthService } from '../../services/auth-service';
import { LoginService } from '../../services/login-service';
import { StatsService } from '../../services/stats-service';
import { ThemeService } from '../../services/theme-service';
import { Navbar } from '../navbar/navbar';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideX } from '@ng-icons/lucide';

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
		Navbar,
		NgIcon,
	],
	providers: [provideIcons({ lucideX })],
	templateUrl: './league.html',
	styleUrl: './league.css',
})
export class League implements OnInit {
	// Determines number of items you'll see within carousel
	constructor(
		private router: Router,
		private loginServe: LoginService,
		private statsService: StatsService,
		private authService: AuthService,
		private themeService: ThemeService,
		private cdRef: ChangeDetectorRef,
	) {}

	public cardList: CardData[] = [];
	public userSearchList: string[] = [];
	public loading: boolean = true;
	public error: boolean = false;
	public emptyListUser: boolean = false;
	public hoveredCardId: string | null = null;
	private currentCard: CardData = {
		id: '',
		title: '',
		users: [],
		description: '',
		buttonText: '',
	};

	ngOnInit(): void {
		this.cardList = this.loginServe.cardList;
		this.userSearchList = this.loginServe.userSearchList;
		this.loading = false;
		this.error = false;

		if (this.loginServe.cardList.length === 0) {
			this.emptyListUser = true;
		} else {
			this.emptyListUser = false;
		}

		this.cdRef.detectChanges();

		console.log(`current username: ${this.loginServe.username}`);

		//console.log("Raw data3 (JSON string):", JSON.stringify(this.cardList, null, 2));
	}

	setHover(id: string): void {
		this.hoveredCardId = id;
	}

	clearHover(): void {
		this.hoveredCardId = null;
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

	removeUserFromSearch(user: string): void {
		console.log('button was pressed');

		// We filter the userSearchList to create a new list that excludes the user string.
		this.loginServe.userSearchList = this.loginServe.userSearchList.filter(
			(existingUser) => existingUser !== user,
		);

		// Iterate over the main cardList and modify the 'users' array within each card.
		this.loginServe.cardList.forEach((card) => {
			// Filter the current card's users list, keeping only those NOT matching the user to be removed.
			card.users = card.users.filter((existingUser) => existingUser !== user);
		});

		// Filter the main cardList to keep only cards where the 'users' array still has content (length > 0).
		this.loginServe.cardList = this.loginServe.cardList.filter(
			(card) => card.users.length > 0,
		);

		// Update this .ts objects version of hte userSearchList and the LoginServe
		this.userSearchList = this.loginServe.userSearchList;
		this.cardList = this.loginServe.cardList;

		// If cardlist has nothing within it, show that the search is empty
		if (this.cardList.length === 0) {
			this.emptyListUser = true;
		}

		console.log(`User ${user} removed from search lists.`);
		console.log('Updated userSearchList:', this.loginServe.userSearchList);
		console.log('Updated cardList:', this.loginServe.cardList);

		// Notify Angular of changes to update the UI
		this.cdRef.detectChanges();
	}

	retrieveLeagues(): void {
		// this.cardList = [];
		if (this.searchControl.valid) {
			// Perform login logic here

			// Check if there's been a change
			this.error = false;
			this.loading = true;
			this.cdRef.detectChanges();

			//console.log("current username " + this.searchControl.value.user)
			this.loginServe.usernameSet(this.searchControl.value.user ?? '');

			// Perform a check to see if the user exists in the backend
			//console.log('Logging in with:', this.searchControl.value.user);
			//this.loginServe.loginUser(this.loginControl.value.user!);

			// Subscribe to what is returned from .getLeagues from the current username
			this.loginServe.getLeagues().subscribe({
				// If no errors, do this
				next: (response) => {
					// From response, pull leagues list, from leagues list, for each league, add them to
					response.leagues.forEach((league) => {
						this.currentCard = {
							id: league.id,
							title: league.name,
							users: [],
							description: `Welcome to the ${league.name} league!`,
							buttonText: 'View',
						};

						// console.log("this is working for league: " + this.currentCard.id);
						this.loginServe.addToCardUserList(
							this.currentCard,
							this.searchControl.value.user ?? '',
						);
					});

					this.loginServe.userSearchList.includes(
						this.searchControl.value.user ?? '',
					)
						? ''
						: this.loginServe.userSearchList.push(
								this.searchControl.value.user ?? '',
							);

					// Reset loading stylings
					this.loading = false;

					if (this.loginServe.cardList.length === 0) {
						this.emptyListUser = true;
					} else {
						this.emptyListUser = false;
					}

					// Detect changes
					this.cdRef.detectChanges();

					// Check userSearchList persists (it does)
					console.log(this.userSearchList);
				},

				// If errors, do this.
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
