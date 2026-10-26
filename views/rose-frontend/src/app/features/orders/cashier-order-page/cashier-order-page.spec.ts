import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CashierOrderPage } from './cashier-order-page';

describe('CashierOrderPage', () => {
  let component: CashierOrderPage;
  let fixture: ComponentFixture<CashierOrderPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CashierOrderPage],
    }).compileComponents();

    fixture = TestBed.createComponent(CashierOrderPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
