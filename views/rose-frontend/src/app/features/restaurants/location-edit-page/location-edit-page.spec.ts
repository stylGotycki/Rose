import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LocationEditPage } from './location-edit-page';

describe('LocationEditPage', () => {
  let component: LocationEditPage;
  let fixture: ComponentFixture<LocationEditPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LocationEditPage],
    }).compileComponents();

    fixture = TestBed.createComponent(LocationEditPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
