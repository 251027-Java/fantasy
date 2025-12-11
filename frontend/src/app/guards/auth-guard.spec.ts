import { TestBed } from '@angular/core/testing';
import { AuthService } from '../services/auth-service';
import { AuthGuard } from './auth-guard';

describe('AuthGuard', () => {
	let guard: AuthGuard;
	let authServiceMock: { isAuthorized: () => boolean };
	const mockSnapshot: any = {};
	const mockState: any = {};

	beforeEach(() => {
		authServiceMock = {
			isAuthorized: () => true,
		};

		TestBed.configureTestingModule({
			providers: [
				AuthGuard,
				{ provide: AuthService, useValue: authServiceMock },
			],
		});
		guard = TestBed.inject(AuthGuard);
	});

	it('should be created', () => {
		expect(guard).toBeTruthy();
	});

	it('should allow activation if user is authorized', () => {
		authServiceMock.isAuthorized = () => true;
		expect(guard.canActivate(mockSnapshot, mockState)).toBe(true);
	});

	it('should deny activation if user is not authorized', () => {
		authServiceMock.isAuthorized = () => false;
		expect(guard.canActivate(mockSnapshot, mockState)).toBe(false);
	});
});
