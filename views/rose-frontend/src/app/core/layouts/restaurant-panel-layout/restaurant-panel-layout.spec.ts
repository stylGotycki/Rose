import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantPanelLayout } from './restaurant-panel-layout';

describe('RestaurantPanelLayout', () => {
  let component: RestaurantPanelLayout;
  let fixture: ComponentFixture<RestaurantPanelLayout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantPanelLayout],
    }).compileComponents();

    fixture = TestBed.createComponent(RestaurantPanelLayout);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
