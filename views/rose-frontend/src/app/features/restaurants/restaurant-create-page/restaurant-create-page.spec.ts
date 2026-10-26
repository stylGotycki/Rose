import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantCreatePage } from './restaurant-create-page';

describe('RestaurantCreatePage', () => {
  let component: RestaurantCreatePage;
  let fixture: ComponentFixture<RestaurantCreatePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantCreatePage],
    }).compileComponents();

    fixture = TestBed.createComponent(RestaurantCreatePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
