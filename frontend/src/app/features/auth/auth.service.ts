import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { AuthState, LoginRequest, LoginResponse } from './auth.model';

const STORAGE_KEY = 'course-eligibility-portal.auth';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly state = signal<AuthState | null>(this.restore());

  readonly isAuthenticated = computed(() => {
    const session = this.state();
    return !!session && session.expiresAt > Date.now();
  });
  readonly username = computed(() => this.state()?.username ?? null);

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>('/auth/login', request)
      .pipe(tap((response) => this.setSession(response)));
  }

  logout(): void {
    this.state.set(null);
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch {
      /* storage unavailable - nothing to clean up */
    }
  }

  token(): string | null {
    const session = this.state();
    return session && session.expiresAt > Date.now() ? session.token : null;
  }

  private setSession(response: LoginResponse): void {
    const session: AuthState = {
      token: response.token,
      username: response.username,
      expiresAt: Date.now() + response.expiresIn * 1000
    };
    this.state.set(session);
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
    } catch {
      /* private browsing / quota exceeded - session-only fallback */
    }
  }

  private restore(): AuthState | null {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (!raw) {
        return null;
      }
      const parsed = JSON.parse(raw) as AuthState;
      return parsed.expiresAt > Date.now() ? parsed : null;
    } catch {
      return null;
    }
  }
}
