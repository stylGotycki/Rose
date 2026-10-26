import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShiftEditPage } from './shift-edit-page';

describe('ShiftEditPage', () => {
  let component: ShiftEditPage;
  let fixture: ComponentFixture<ShiftEditPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShiftEditPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ShiftEditPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
