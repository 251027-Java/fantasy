import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestScores } from './test-scores';

describe('TestScores', () => {
	let component: TestScores;
	let fixture: ComponentFixture<TestScores>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [TestScores],
		}).compileComponents();

		fixture = TestBed.createComponent(TestScores);
		component = fixture.componentInstance;
		await fixture.whenStable();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
