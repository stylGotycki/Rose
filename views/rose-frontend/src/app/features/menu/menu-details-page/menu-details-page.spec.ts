import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuDetailsPage } from './menu-details-page';

describe('MenuDetailsPage', () => {
  let component: MenuDetailsPage;
  let fixture: ComponentFixture<MenuDetailsPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuDetailsPage],
    }).compileComponents();

    fixture = TestBed.createComponent(MenuDetailsPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
