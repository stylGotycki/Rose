import { Routes } from '@angular/router';

import { AppLayout } from './core/layouts/app-layout/app-layout';
import { PublicLayout } from './core/layouts/public-layout/public-layout';
import { RestaurantPanelLayout } from './core/layouts/restaurant-panel-layout/restaurant-panel-layout';

import { authGuard } from './core/guards/auth-guard';
import { guestGuard } from './core/guards/guest-guard';
import { restaurantSelectedGuard } from './core/guards/restaurant-selected-guard';

import { ForgotPasswordPage } from './features/auth/forgot-password-page/forgot-password-page';
import { LoginPage } from './features/auth/login-page/login-page';
import { RegisterPage } from './features/auth/register-page/register-page';
import { ResetPasswordPage } from './features/auth/reset-password-page/reset-password-page';
import { VerifyPage } from './features/auth/verify-page/verify-page';

import { BillListPage } from './features/billing/bill-list-page/bill-list-page';

import { EmployeeCreatePage } from './features/employees/employee-create-page/employee-create-page';
import { EmployeeEditPage } from './features/employees/employee-edit-page/employee-edit-page';
import { EmployeeListPage } from './features/employees/employee-list-page/employee-list-page';

import { KitchenBoardPage } from './features/kitchen/kitchen-board-page/kitchen-board-page';

import { MemberCreatePage } from './features/members/member-create-page/member-create-page';
import { MemberEditPage } from './features/members/member-edit-page/member-edit-page';
import { MemberListPage } from './features/members/member-list-page/member-list-page';

import { CategoryCreatePage } from './features/menu/category-create-page/category-create-page';
import { CategoryEditPage } from './features/menu/category-edit-page/category-edit-page';
import { ItemCreatePage } from './features/menu/item-create-page/item-create-page';
import { ItemEditPage } from './features/menu/item-edit-page/item-edit-page';
import { MenuCreatePage } from './features/menu/menu-create-page/menu-create-page';
import { MenuDetailsPage } from './features/menu/menu-details-page/menu-details-page';
import { MenuEditPage } from './features/menu/menu-edit-page/menu-edit-page';
import { MenuListPage } from './features/menu/menu-list-page/menu-list-page';

import { CashierOrderPage } from './features/orders/cashier-order-page/cashier-order-page';
import { OrderListPage } from './features/orders/order-list-page/order-list-page';

import { AboutPage } from './features/public/about-page/about-page';
import { WelcomePage } from './features/public/welcome-page/welcome-page';

import { PanelHomePage } from './features/restaurant-panel/panel-home-page/panel-home-page';
import { PanelSettingsPage } from './features/restaurant-panel/panel-settings-page/panel-settings-page';

import { LocationCreatePage } from './features/restaurants/location-create-page/location-create-page';
import { LocationEditPage } from './features/restaurants/location-edit-page/location-edit-page';
import { LocationListPage } from './features/restaurants/location-list-page/location-list-page';
import { RestaurantCreatePage } from './features/restaurants/restaurant-create-page/restaurant-create-page';
import { RestaurantEditPage } from './features/restaurants/restaurant-edit-page/restaurant-edit-page';
import { RestaurantListPage } from './features/restaurants/restaurant-list-page/restaurant-list-page';

import { SchedulePage } from './features/schedule/schedule-page/schedule-page';
import { ShiftCreatePage } from './features/schedule/shift-create-page/shift-create-page';
import { ShiftEditPage } from './features/schedule/shift-edit-page/shift-edit-page';

export const routes: Routes = [
  {
    path: '',
    component: PublicLayout,
    children: [
      {
        path: '',
        component: WelcomePage,
      },
      {
        path: 'about',
        component: AboutPage,
      },
      {
        path: 'login',
        component: LoginPage,
        canActivate: [guestGuard],
      },
      {
        path: 'register',
        component: RegisterPage,
        canActivate: [guestGuard],
      },
      {
        path: 'verify',
        component: VerifyPage,
      },
      {
        path: 'forgot-password',
        component: ForgotPasswordPage,
      },
      {
        path: 'reset-password',
        component: ResetPasswordPage,
      },
    ],
  },
  {
    path: 'app',
    component: AppLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'restaurants',
      },
      {
        path: 'restaurants',
        component: RestaurantListPage,
      },
      {
        path: 'restaurants/create',
        component: RestaurantCreatePage,
      },
      {
        path: 'restaurants/:restaurantId/edit',
        component: RestaurantEditPage,
      },
    ],
  },
  {
    path: 'app/restaurants/:restaurantId/panel',
    component: RestaurantPanelLayout,
    canActivate: [
      authGuard,
      restaurantSelectedGuard,
    ],
    children: [
      {
        path: '',
        component: PanelHomePage,
      },
      {
        path: 'settings',
        component: PanelSettingsPage,
      },

      {
        path: 'locations',
        component: LocationListPage,
      },
      {
        path: 'locations/create',
        component: LocationCreatePage,
      },
      {
        path: 'locations/:locationId/edit',
        component: LocationEditPage,
      },

      {
        path: 'members',
        component: MemberListPage,
      },
      {
        path: 'members/create',
        component: MemberCreatePage,
      },
      {
        path: 'members/:memberId/edit',
        component: MemberEditPage,
      },

      {
        path: 'employees',
        component: EmployeeListPage,
      },
      {
        path: 'employees/create',
        component: EmployeeCreatePage,
      },
      {
        path: 'employees/:employeeId/edit',
        component: EmployeeEditPage,
      },

      {
        path: 'schedule',
        component: SchedulePage,
      },
      {
        path: 'schedule/create',
        component: ShiftCreatePage,
      },
      {
        path: 'schedule/:shiftId/edit',
        component: ShiftEditPage,
      },

      {
        path: 'menu',
        component: MenuListPage,
      },
      {
        path: 'menu/create',
        component: MenuCreatePage,
      },
      {
        path: 'menu/:menuId/categories/create',
        component: CategoryCreatePage,
      },
      {
        path: 'menu/:menuId/categories/:categoryId/items/create',
        component: ItemCreatePage,
      },
      {
        path: 'menu/:menuId/categories/:categoryId/items/:itemId/edit',
        component: ItemEditPage,
      },
      {
        path: 'menu/:menuId/categories/:categoryId/edit',
        component: CategoryEditPage,
      },
      {
        path: 'menu/:menuId/edit',
        component: MenuEditPage,
      },
      {
        path: 'menu/:menuId/details',
        component: MenuDetailsPage,
      },

      {
        path: 'cashier',
        component: CashierOrderPage,
      },
      {
        path: 'orders',
        component: OrderListPage,
      },

      {
        path: 'kitchen',
        component: KitchenBoardPage,
      },
      {
        path: 'billing',
        component: BillListPage,
      },
    ],
  },
  {
    path: '**',
    redirectTo: '',
  },
];