import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployeeEditPage } from './employee-edit-page';

describe('EmployeeEditPage', () => {
  let component: EmployeeEditPage;
  let fixture: ComponentFixture<EmployeeEditPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmployeeEditPage],
    }).compileComponents();

    fixture = TestBed.createComponent(EmployeeEditPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
