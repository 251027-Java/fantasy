import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
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

@Component({
	selector: 'app-league',
	imports: [
		HlmFormFieldImports,
		HlmSelectImports,
		HlmSelectImports,
		BrnSelectImports,
		HlmButtonImports,
		ReactiveFormsModule,
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
		private cdRef: ChangeDetectorRef,
	) {}

	public cardList: CardData[] = [];
	public loading: boolean = true;

	ngOnInit(): void {
		this.loginServe.getLeagues().subscribe((data) => {
			this.cardList = data.leagues.map((league) => ({
				id: league.id,
				title: league.name,
				description: `Welcome to the ${league.name} league!`,
				buttonText: 'View',
			}));

			this.loading = false;
			this.cdRef.detectChanges();
			//console.log("data coming in: " + this.cardList);
			console.log(
				'Raw data2 (JSON string):',
				JSON.stringify(this.cardList, null, 2),
			);
		});

		//console.log("Raw data3 (JSON string):", JSON.stringify(this.cardList, null, 2));
	}

	Logout(): void {
		this.loginServe.logout();
		this.router.navigateByUrl('');
	}

	viewLeague(card: CardData): void {
		console.log('Viewing league with id:', card.id);
		this.statsService.setLeague(String(card.id), card.title);
		this.router.navigateByUrl('stats');
	}
}
