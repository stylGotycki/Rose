import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployeeListPage } from './employee-list-page';

describe('EmployeeListPage', () => {
  let component: EmployeeListPage;
  let fixture: ComponentFixture<EmployeeListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmployeeListPage],
    }).compileComponents();

    fixture = TestBed.createComponent(EmployeeListPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
