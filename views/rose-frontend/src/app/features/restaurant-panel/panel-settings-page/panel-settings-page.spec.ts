import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PanelSettingsPage } from './panel-settings-page';

describe('PanelSettingsPage', () => {
  let component: PanelSettingsPage;
  let fixture: ComponentFixture<PanelSettingsPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PanelSettingsPage],
    }).compileComponents();

    fixture = TestBed.createComponent(PanelSettingsPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
