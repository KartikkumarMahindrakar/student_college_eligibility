import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { CourseInfo, StreamCourses } from './eligibility.model';

@Injectable({ providedIn: 'root' })
export class CourseService {
  private readonly http = inject(HttpClient);
  private readonly groups = signal<StreamCourses[]>([]);

  readonly courseGroups = this.groups.asReadonly();
  readonly allCourses = computed(() => this.groups().flatMap((g) => g.courses));

  constructor() {
    this.http.get<StreamCourses[]>('/courses').subscribe((groups) => this.groups.set(groups));
  }

  findByName(name: string | null | undefined): CourseInfo | null {
    if (!name) {
      return null;
    }
    return this.allCourses().find((c) => c.name === name) ?? null;
  }
}
