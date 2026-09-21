import { Component, computed, inject, signal } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData } from 'chart.js';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { StatisticsService } from './statistics.service';
import { Statistics } from './statistics.model';

@Component({
  selector: 'app-statistics-page',
  imports: [BaseChartDirective, MatCardModule, MatProgressSpinnerModule],
  templateUrl: './statistics-page.html',
  styleUrl: './statistics-page.scss'
})
export class StatisticsPage {
  private readonly statisticsService = inject(StatisticsService);

  readonly loading = signal(true);
  readonly stats = signal<Statistics | null>(null);

  readonly eligibilityChartData = computed<ChartData<'doughnut'>>(() => {
    const s = this.stats();
    return {
      labels: ['Eligible', 'Not Eligible'],
      datasets: [
        {
          data: [s?.eligibleCount ?? 0, s?.notEligibleCount ?? 0],
          backgroundColor: ['#2e7d32', '#b3261e']
        }
      ]
    };
  });

  readonly courseChartData = computed<ChartData<'bar'>>(() => {
    const counts = this.stats()?.courseWiseCounts ?? [];
    return {
      labels: counts.map((c) => c.courseName),
      datasets: [
        { label: 'Eligible', data: counts.map((c) => c.eligible), backgroundColor: '#2e7d32' },
        { label: 'Not Eligible', data: counts.map((c) => c.notEligible), backgroundColor: '#b3261e' }
      ]
    };
  });

  readonly barOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    scales: { y: { beginAtZero: true, ticks: { precision: 0 } } }
  };

  readonly doughnutOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    maintainAspectRatio: false
  };

  constructor() {
    this.statisticsService.get().subscribe({
      next: (stats) => {
        this.stats.set(stats);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}
