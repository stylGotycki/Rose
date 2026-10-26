import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemberEditPage } from './member-edit-page';

describe('MemberEditPage', () => {
  let component: MemberEditPage;
  let fixture: ComponentFixture<MemberEditPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MemberEditPage],
    }).compileComponents();

    fixture = TestBed.createComponent(MemberEditPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
