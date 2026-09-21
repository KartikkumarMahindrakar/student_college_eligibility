import { Injectable, signal } from '@angular/core';
import { EligibilityResult } from './eligibility.model';

/** In-memory handoff from the submit form to the result page - avoids a redundant round trip
 * right after submit. The result page falls back to EligibilityService.getById() when empty
 * (hard refresh, deep link, shared URL). */
@Injectable({ providedIn: 'root' })
export class EligibilityResultStore {
  private readonly result = signal<EligibilityResult | null>(null);
  readonly current = this.result.asReadonly();

  set(result: EligibilityResult): void {
    this.result.set(result);
  }

  clear(): void {
    this.result.set(null);
  }
}
