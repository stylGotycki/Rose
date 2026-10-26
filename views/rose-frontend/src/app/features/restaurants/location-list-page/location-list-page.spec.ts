import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LocationListPage } from './location-list-page';

describe('LocationListPage', () => {
  let component: LocationListPage;
  let fixture: ComponentFixture<LocationListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LocationListPage],
    }).compileComponents();

    fixture = TestBed.createComponent(LocationListPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
