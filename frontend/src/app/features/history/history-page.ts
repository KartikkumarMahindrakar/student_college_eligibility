import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { CourseService } from '../eligibility/course.service';
import { HistoryService } from './history.service';
import { FileDownloadService } from './file-download.service';
import { HistorySummary } from './history.model';

@Component({
  selector: 'app-history-page',
  imports: [
    ReactiveFormsModule,
    DatePipe,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatTableModule,
    MatProgressSpinnerModule,
    MatIconModule
  ],
  templateUrl: './history-page.html',
  styleUrl: './history-page.scss'
})
export class HistoryPage {
  private readonly fb = inject(FormBuilder);
  private readonly historyService = inject(HistoryService);
  private readonly fileDownloadService = inject(FileDownloadService);
  protected readonly courseService = inject(CourseService);

  readonly displayedColumns = ['studentName', 'courseApplied', 'eligibilityStatus', 'date'];
  readonly rows = signal<HistorySummary[]>([]);
  readonly loading = signal(false);
  readonly exporting = signal(false);

  readonly filterForm = this.fb.nonNullable.group({
    name: [''],
    course: [''],
    status: [''],
    from: [''],
    to: ['']
  });

  constructor() {
    this.search();
  }

  search(): void {
    this.loading.set(true);
    this.historyService.search(this.filterForm.getRawValue()).subscribe({
      next: (rows) => {
        this.rows.set(rows);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  resetFilters(): void {
    this.filterForm.reset({ name: '', course: '', status: '', from: '', to: '' });
    this.search();
  }

  exportExcel(): void {
    this.exporting.set(true);
    this.historyService.exportExcel(this.filterForm.getRawValue()).subscribe({
      next: (response) => {
        this.exporting.set(false);
        this.fileDownloadService.saveFromResponse(response, 'submission-history.xlsx');
      },
      error: () => this.exporting.set(false)
    });
  }

  exportPdf(): void {
    this.exporting.set(true);
    this.historyService.exportPdf(this.filterForm.getRawValue()).subscribe({
      next: (response) => {
        this.exporting.set(false);
        this.fileDownloadService.saveFromResponse(response, 'submission-history.pdf');
      },
      error: () => this.exporting.set(false)
    });
  }
}
