import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuListPage } from './menu-list-page';

describe('MenuListPage', () => {
  let component: MenuListPage;
  let fixture: ComponentFixture<MenuListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuListPage],
    }).compileComponents();

    fixture = TestBed.createComponent(MenuListPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
