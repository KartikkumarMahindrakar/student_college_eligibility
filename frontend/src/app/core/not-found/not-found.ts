import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-not-found',
  imports: [RouterLink, MatButtonModule],
  template: `
    <div class="not-found">
      <h1>404</h1>
      <p>That page doesn't exist.</p>
      <a mat-flat-button color="primary" routerLink="/apply">Go to the application form</a>
    </div>
  `,
  styles: `
    .not-found {
      text-align: center;
      padding: 64px 16px;
    }
  `
})
export class NotFound {}
