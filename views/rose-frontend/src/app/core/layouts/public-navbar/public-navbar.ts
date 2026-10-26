import { Component, inject } from '@angular/core';
import {
  Router,
  RouterLink,
  RouterLinkActive,
} from '@angular/router';

import { Auth } from '../../auth/auth';

@Component({
  selector: 'app-public-navbar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './public-navbar.html',
  styleUrl: './public-navbar.css',
})
export class PublicNavbar {
  readonly auth = inject(Auth);

  private readonly router = inject(Router);

  logout(): void {
    this.auth.logout().subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: () => {
        this.auth.clearLocalSession();
        this.router.navigate(['/']);
      },
    });
  }
}