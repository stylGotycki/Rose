import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShiftCreatePage } from './shift-create-page';

describe('ShiftCreatePage', () => {
  let component: ShiftCreatePage;
  let fixture: ComponentFixture<ShiftCreatePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShiftCreatePage],
    }).compileComponents();

    fixture = TestBed.createComponent(ShiftCreatePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
