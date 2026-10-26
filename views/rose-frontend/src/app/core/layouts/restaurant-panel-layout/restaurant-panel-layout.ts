import {
  Component,
  OnDestroy,
  OnInit,
  inject,
} from '@angular/core';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';

import { RestaurantSidebar } from '../restaurant-sidebar/restaurant-sidebar';
import { RestaurantTopbar } from '../restaurant-topbar/restaurant-topbar';
import { RestaurantContext } from '../../state/restaurant-context';

@Component({
  selector: 'app-restaurant-panel-layout',
  imports: [
    RouterOutlet,
    RestaurantSidebar,
    RestaurantTopbar,
  ],
  templateUrl: './restaurant-panel-layout.html',
  styleUrl: './restaurant-panel-layout.css',
})
export class RestaurantPanelLayout implements OnInit, OnDestroy {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly restaurantContext = inject(RestaurantContext);

  ngOnInit(): void {
    const restaurantId =
      this.route.snapshot.paramMap.get('restaurantId');

    if (!restaurantId) {
      this.router.navigate(['/app/restaurants']);
      return;
    }

    this.restaurantContext.load(restaurantId);
  }

  ngOnDestroy(): void {
    this.restaurantContext.clear();
  }

  backToRestaurants(): void {
    this.router.navigate(['/app/restaurants']);
  }
}