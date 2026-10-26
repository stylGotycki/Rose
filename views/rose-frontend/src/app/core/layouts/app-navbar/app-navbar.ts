import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { Auth } from '../../auth/auth';

@Component({
  selector: 'app-app-navbar',
  imports: [RouterLink],
  templateUrl: './app-navbar.html',
  styleUrl: './app-navbar.css',
})
export class AppNavbar {
  readonly auth = inject(Auth);

  private readonly router = inject(Router);

  logout(): void {
    this.auth.logout().subscribe({
      next: () => this.router.navigate(['/']),
      error: () => {
        this.auth.clearLocalSession();
        this.router.navigate(['/']);
      },
    });
  }
}