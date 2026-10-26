import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantTopbar } from './restaurant-topbar';

describe('RestaurantTopbar', () => {
  let component: RestaurantTopbar;
  let fixture: ComponentFixture<RestaurantTopbar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantTopbar],
    }).compileComponents();

    fixture = TestBed.createComponent(RestaurantTopbar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
