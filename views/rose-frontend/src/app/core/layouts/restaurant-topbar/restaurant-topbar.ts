import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { Auth } from '../../auth/auth';
import { RestaurantContext } from '../../state/restaurant-context';

@Component({
  selector: 'app-restaurant-topbar',
  imports: [RouterLink],
  templateUrl: './restaurant-topbar.html',
  styleUrl: './restaurant-topbar.css',
})
export class RestaurantTopbar {
  readonly auth = inject(Auth);
  readonly restaurantContext = inject(RestaurantContext);

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