import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillListPage } from './bill-list-page';

describe('BillListPage', () => {
  let component: BillListPage;
  let fixture: ComponentFixture<BillListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BillListPage],
    }).compileComponents();

    fixture = TestBed.createComponent(BillListPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
