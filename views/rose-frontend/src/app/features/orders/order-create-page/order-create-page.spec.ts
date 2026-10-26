import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderCreatePage } from './order-create-page';

describe('OrderCreatePage', () => {
  let component: OrderCreatePage;
  let fixture: ComponentFixture<OrderCreatePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderCreatePage],
    }).compileComponents();

    fixture = TestBed.createComponent(OrderCreatePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
