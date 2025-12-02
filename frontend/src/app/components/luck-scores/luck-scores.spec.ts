import { type ComponentFixture, TestBed } from '@angular/core/testing';

import { LuckScores } from './luck-scores';

describe('LuckScores', () => {
	let component: LuckScores;
	let fixture: ComponentFixture<LuckScores>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [LuckScores],
		}).compileComponents();

		fixture = TestBed.createComponent(LuckScores);
		component = fixture.componentInstance;
		await fixture.whenStable();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
