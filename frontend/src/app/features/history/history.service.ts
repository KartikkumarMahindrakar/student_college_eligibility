import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HistoryFilters, HistorySummary } from './history.model';

@Injectable({ providedIn: 'root' })
export class HistoryService {
  private readonly http = inject(HttpClient);

  search(filters: HistoryFilters): Observable<HistorySummary[]> {
    return this.http.get<HistorySummary[]>('/students/history', { params: this.buildParams(filters) });
  }

  exportExcel(filters: HistoryFilters): Observable<HttpResponse<Blob>> {
    return this.http.get('/students/history/export/excel', {
      params: this.buildParams(filters),
      responseType: 'blob',
      observe: 'response'
    });
  }

  exportPdf(filters: HistoryFilters): Observable<HttpResponse<Blob>> {
    return this.http.get('/students/history/export/pdf', {
      params: this.buildParams(filters),
      responseType: 'blob',
      observe: 'response'
    });
  }

  private buildParams(filters: HistoryFilters): HttpParams {
    let params = new HttpParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value) {
        params = params.set(key, value);
      }
    });
    return params;
  }
}
