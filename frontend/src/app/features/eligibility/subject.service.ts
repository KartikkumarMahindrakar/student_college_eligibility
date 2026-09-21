import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class SubjectService {
  private readonly http = inject(HttpClient);
  private readonly subjectsSignal = signal<string[]>([]);
  readonly subjects = this.subjectsSignal.asReadonly();

  constructor() {
    this.http.get<string[]>('/subjects').subscribe((subjects) => this.subjectsSignal.set(subjects));
  }
}
