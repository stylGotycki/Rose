import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuCreatePage } from './menu-create-page';

describe('MenuCreatePage', () => {
  let component: MenuCreatePage;
  let fixture: ComponentFixture<MenuCreatePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuCreatePage],
    }).compileComponents();

    fixture = TestBed.createComponent(MenuCreatePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
