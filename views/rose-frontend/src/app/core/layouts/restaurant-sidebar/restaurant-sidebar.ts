import {
  Component,
  inject,
} from '@angular/core';
import {
  RouterLink,
  RouterLinkActive,
} from '@angular/router';

import { RestaurantContext } from '../../state/restaurant-context';

interface PanelLink {
  label: string;
  path: string;
}

@Component({
  selector: 'app-restaurant-sidebar',
  imports: [
    RouterLink,
    RouterLinkActive,
  ],
  templateUrl: './restaurant-sidebar.html',
  styleUrl: './restaurant-sidebar.css',
})
export class RestaurantSidebar {
  readonly restaurantContext = inject(
    RestaurantContext,
  );

  readonly links: PanelLink[] = [
    {
      label: 'Start',
      path: '',
    },
    {
      label: 'Konfiguracja',
      path: 'settings',
    },
    {
      label: 'Lokale',
      path: 'locations',
    },
    {
      label: 'Dostępy',
      path: 'members',
    },
    {
      label: 'Pracownicy',
      path: 'employees',
    },
    {
      label: 'Grafik',
      path: 'schedule',
    },
    {
      label: 'Menu',
      path: 'menu',
    },
    {
      label: 'Kasa',
      path: 'cashier',
    },
    {
      label: 'Zamówienia',
      path: 'orders',
    },
    {
      label: 'Kuchnia',
      path: 'kitchen',
    },
    {
      label: 'Rachunki',
      path: 'billing',
    },
  ];

  buildLink(
    path: string,
  ): string {
    const restaurantId =
      this.restaurantContext.restaurantId();

    if (!restaurantId) {
      return '/app/restaurants';
    }

    const panelBase =
      `/app/restaurants/${restaurantId}/panel`;

    return path
      ? `${panelBase}/${path}`
      : panelBase;
  }
}