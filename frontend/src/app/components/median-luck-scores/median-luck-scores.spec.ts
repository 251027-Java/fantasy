import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MedianLuckScores } from './median-luck-scores';

describe('MedianLuckScores', () => {
	let component: MedianLuckScores;
	let fixture: ComponentFixture<MedianLuckScores>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MedianLuckScores],
		}).compileComponents();

		fixture = TestBed.createComponent(MedianLuckScores);
		component = fixture.componentInstance;
		await fixture.whenStable();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
