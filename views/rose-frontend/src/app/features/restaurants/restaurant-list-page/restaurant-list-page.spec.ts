import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantListPage } from './restaurant-list-page';

describe('RestaurantListPage', () => {
  let component: RestaurantListPage;
  let fixture: ComponentFixture<RestaurantListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantListPage],
    }).compileComponents();

    fixture = TestBed.createComponent(RestaurantListPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
