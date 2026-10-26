import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemberCreatePage } from './member-create-page';

describe('MemberCreatePage', () => {
  let component: MemberCreatePage;
  let fixture: ComponentFixture<MemberCreatePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MemberCreatePage],
    }).compileComponents();

    fixture = TestBed.createComponent(MemberCreatePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
