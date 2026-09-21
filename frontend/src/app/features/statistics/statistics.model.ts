export interface CourseWiseCount {
  courseName: string;
  total: number;
  eligible: number;
  notEligible: number;
}

export interface Statistics {
  totalSubmissions: number;
  eligibleCount: number;
  notEligibleCount: number;
  courseWiseCounts: CourseWiseCount[];
}
