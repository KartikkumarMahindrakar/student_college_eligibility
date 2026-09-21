import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CheckEligibilityRequest, EligibilityResult } from './eligibility.model';

@Injectable({ providedIn: 'root' })
export class EligibilityService {
  private readonly http = inject(HttpClient);

  submit(request: CheckEligibilityRequest): Observable<EligibilityResult> {
    return this.http.post<EligibilityResult>('/students/check-eligibility', request);
  }

  getById(id: number): Observable<EligibilityResult> {
    return this.http.get<EligibilityResult>(`/students/check-eligibility/${id}`);
  }
}
