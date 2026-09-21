export interface CourseInfo {
  name: string;
  requiredSubjects: string[];
  cutoffPercentage: number | null;
  requiresJee: boolean;
  requiresNeet: boolean;
}

export interface StreamCourses {
  stream: string;
  courses: CourseInfo[];
}

export interface SubjectMarkEntry {
  subjectName: string;
  marks: number;
}

export interface CheckEligibilityRequest {
  studentName: string;
  age: number;
  gender: string;
  subjectMarks: SubjectMarkEntry[];
  jeeQualified: boolean;
  neetQualified: boolean;
  desiredCourse: string;
}

export interface EligibilityResult {
  id: number;
  studentName: string;
  desiredCourse: string;
  eligible: boolean;
  reason: string;
  recommendedCourses: string[];
  evaluatedAt: string;
}
