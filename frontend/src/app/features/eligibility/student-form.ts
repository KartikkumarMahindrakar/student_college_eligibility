import { Component, computed, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { CourseService } from './course.service';
import { SubjectService } from './subject.service';
import { EligibilityService } from './eligibility.service';
import { EligibilityResultStore } from './eligibility-result.store';

const NAME_PATTERN = /^[A-Za-z]+( [A-Za-z]+)*$/;
const SUBJECT_COUNT = 6;

export function distinctSubjectsValidator(control: AbstractControl): ValidationErrors | null {
  const rows = (control as FormArray).controls;
  const names = rows.map((row) => row.get('subjectName')?.value).filter((value) => !!value);
  if (names.length < 2) {
    return null;
  }
  return new Set(names).size === names.length ? null : { duplicateSubjects: true };
}

@Component({
  selector: 'app-student-form',
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatCheckboxModule,
    MatProgressSpinnerModule,
    MatIconModule
  ],
  templateUrl: './student-form.html',
  styleUrl: './student-form.scss'
})
export class StudentForm {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly eligibilityService = inject(EligibilityService);
  private readonly resultStore = inject(EligibilityResultStore);
  protected readonly courseService = inject(CourseService);
  protected readonly subjectService = inject(SubjectService);

  readonly submitting = signal(false);
  readonly serverError = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    personal: this.fb.nonNullable.group({
      studentName: ['', [Validators.required, Validators.pattern(NAME_PATTERN)]],
      age: [null as number | null, [Validators.required, Validators.min(17), Validators.max(25)]],
      gender: ['', Validators.required]
    }),
    academic: this.fb.nonNullable.group({
      subjectMarks: this.fb.array(
        Array.from({ length: SUBJECT_COUNT }, () => this.buildSubjectRow()),
        { validators: distinctSubjectsValidator }
      ),
      jeeQualified: [false],
      neetQualified: [false],
      desiredCourse: ['', Validators.required]
    })
  });

  get subjectRows(): FormArray {
    return this.form.get('academic.subjectMarks') as FormArray;
  }

  private readonly subjectMarksValue = toSignal(this.subjectRows.valueChanges, {
    initialValue: this.subjectRows.value as { subjectName: string }[]
  });

  readonly chosenSubjects = computed(() => {
    const values = this.subjectMarksValue();
    const names = values
      .map((v: { subjectName?: string | null }) => v?.subjectName)
      .filter((v?: string | null): v is string => !!v);
    return new Set(names);
  });

  private readonly desiredCourseControl = this.form.get('academic.desiredCourse')!;
  private readonly desiredCourseValue = toSignal(this.desiredCourseControl.valueChanges, {
    initialValue: this.desiredCourseControl.value as string
  });

  readonly selectedCourse = computed(() => this.courseService.findByName(this.desiredCourseValue()));

  isChosenElsewhere(subject: string, rowIndex: number): boolean {
    const row = this.subjectRows.at(rowIndex);
    if (row.get('subjectName')?.value === subject) {
      return false;
    }
    return this.chosenSubjects().has(subject);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitting.set(true);
    this.serverError.set(null);

    const raw = this.form.getRawValue();
    this.eligibilityService
      .submit({
        studentName: raw.personal.studentName.trim(),
        age: raw.personal.age as number,
        gender: raw.personal.gender,
        subjectMarks: raw.academic.subjectMarks.map((row) => ({
          subjectName: row.subjectName,
          marks: row.marks as number
        })),
        jeeQualified: raw.academic.jeeQualified,
        neetQualified: raw.academic.neetQualified,
        desiredCourse: raw.academic.desiredCourse
      })
      .subscribe({
        next: (result) => {
          this.resultStore.set(result);
          this.router.navigate(['/result', result.id]);
        },
        error: (err) => {
          this.submitting.set(false);
          const fieldErrors = err?.error?.fieldErrors as Record<string, string> | undefined;
          if (fieldErrors && Object.keys(fieldErrors).length > 0) {
            this.serverError.set(Object.values(fieldErrors).join(' '));
          } else {
            this.serverError.set(err?.error?.message ?? 'Something went wrong. Please try again.');
          }
        }
      });
  }

  private buildSubjectRow() {
    return this.fb.nonNullable.group({
      subjectName: ['', Validators.required],
      marks: [null as number | null, [Validators.required, Validators.min(0), Validators.max(100)]]
    });
  }
}
