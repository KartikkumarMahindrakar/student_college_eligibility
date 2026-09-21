import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('is not authenticated before logging in', () => {
    expect(service.isAuthenticated()).toBe(false);
    expect(service.token()).toBeNull();
  });

  it('login stores the session and marks the user authenticated', () => {
    service.login({ username: 'admin', password: 'Admin@123' }).subscribe();

    const req = httpMock.expectOne('/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush({ token: 'abc.def.ghi', expiresIn: 3600, username: 'admin' });

    expect(service.isAuthenticated()).toBe(true);
    expect(service.token()).toBe('abc.def.ghi');
    expect(service.username()).toBe('admin');
  });

  it('logout clears the session', () => {
    service.login({ username: 'admin', password: 'Admin@123' }).subscribe();
    httpMock.expectOne('/auth/login').flush({ token: 'abc.def.ghi', expiresIn: 3600, username: 'admin' });

    service.logout();

    expect(service.isAuthenticated()).toBe(false);
    expect(service.token()).toBeNull();
    expect(service.username()).toBeNull();
  });
});
