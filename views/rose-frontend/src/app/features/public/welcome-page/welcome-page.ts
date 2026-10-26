import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Auth } from '../../../core/auth/auth';

@Component({
  selector: 'app-welcome-page',
  imports: [RouterLink],
  templateUrl: './welcome-page.html',
  styleUrl: './welcome-page.css',
})
export class WelcomePage {
  readonly auth = inject(Auth);
}