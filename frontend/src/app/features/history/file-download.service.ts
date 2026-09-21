import { HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class FileDownloadService {
  saveFromResponse(response: HttpResponse<Blob>, fallbackFilename: string): void {
    const blob = response.body;
    if (!blob) {
      return;
    }
    const filename = this.parseFilename(response.headers.get('Content-Disposition')) ?? fallbackFilename;
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    window.URL.revokeObjectURL(url);
  }

  private parseFilename(disposition: string | null): string | null {
    if (!disposition) {
      return null;
    }
    const match = /filename="?([^";]+)"?/.exec(disposition);
    return match ? match[1] : null;
  }
}
