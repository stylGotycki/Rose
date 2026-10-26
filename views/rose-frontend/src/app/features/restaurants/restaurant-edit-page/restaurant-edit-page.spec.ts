import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantEditPage } from './restaurant-edit-page';

describe('RestaurantEditPage', () => {
  let component: RestaurantEditPage;
  let fixture: ComponentFixture<RestaurantEditPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantEditPage],
    }).compileComponents();

    fixture = TestBed.createComponent(RestaurantEditPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
