import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PanelHomePage } from './panel-home-page';

describe('PanelHomePage', () => {
  let component: PanelHomePage;
  let fixture: ComponentFixture<PanelHomePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PanelHomePage],
    }).compileComponents();

    fixture = TestBed.createComponent(PanelHomePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
