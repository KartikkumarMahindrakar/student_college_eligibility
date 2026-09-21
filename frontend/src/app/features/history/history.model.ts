export interface HistorySummary {
  id: number;
  studentName: string;
  courseApplied: string;
  eligibilityStatus: string;
  date: string;
}

export interface HistoryFilters {
  name?: string;
  course?: string;
  status?: string;
  from?: string;
  to?: string;
}
