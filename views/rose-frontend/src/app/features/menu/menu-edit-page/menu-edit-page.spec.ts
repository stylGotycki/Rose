import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuEditPage } from './menu-edit-page';

describe('MenuEditPage', () => {
  let component: MenuEditPage;
  let fixture: ComponentFixture<MenuEditPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuEditPage],
    }).compileComponents();

    fixture = TestBed.createComponent(MenuEditPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
