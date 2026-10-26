import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { RestaurantContext } from '../../../core/state/restaurant-context';

@Component({
  selector: 'app-panel-home-page',
  imports: [RouterLink],
  templateUrl: './panel-home-page.html',
  styleUrl: './panel-home-page.css',
})
export class PanelHomePage {
  readonly restaurantContext = inject(RestaurantContext);

  panelLink(path: string): string[] {
    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      return ['/app/restaurants'];
    }

    return [
      '/app/restaurants',
      restaurantId,
      'panel',
      path,
    ];
  }
}