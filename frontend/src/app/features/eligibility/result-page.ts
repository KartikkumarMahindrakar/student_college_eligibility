import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { EligibilityService } from './eligibility.service';
import { EligibilityResultStore } from './eligibility-result.store';
import { EligibilityResult } from './eligibility.model';

@Component({
  selector: 'app-result-page',
  imports: [RouterLink, MatCardModule, MatButtonModule, MatIconModule, MatChipsModule, MatProgressSpinnerModule],
  templateUrl: './result-page.html',
  styleUrl: './result-page.scss'
})
export class ResultPage {
  private readonly route = inject(ActivatedRoute);
  private readonly eligibilityService = inject(EligibilityService);
  private readonly store = inject(EligibilityResultStore);

  readonly loading = signal(false);
  readonly notFound = signal(false);
  readonly result = signal<EligibilityResult | null>(null);

  constructor() {
    const stored = this.store.current();
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (stored && stored.id === id) {
      this.result.set(stored);
      return;
    }

    if (!id || Number.isNaN(id)) {
      this.notFound.set(true);
      return;
    }

    // Hard refresh / deep link / shared URL - fall back to the public single-result lookup.
    this.loading.set(true);
    this.eligibilityService.getById(id).subscribe({
      next: (result) => {
        this.loading.set(false);
        this.result.set(result);
      },
      error: () => {
        this.loading.set(false);
        this.notFound.set(true);
      }
    });
  }
}
