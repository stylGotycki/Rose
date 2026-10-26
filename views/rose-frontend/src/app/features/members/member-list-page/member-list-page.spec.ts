import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemberListPage } from './member-list-page';

describe('MemberListPage', () => {
  let component: MemberListPage;
  let fixture: ComponentFixture<MemberListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MemberListPage],
    }).compileComponents();

    fixture = TestBed.createComponent(MemberListPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
