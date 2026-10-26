import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LocationCreatePage } from './location-create-page';

describe('LocationCreatePage', () => {
  let component: LocationCreatePage;
  let fixture: ComponentFixture<LocationCreatePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LocationCreatePage],
    }).compileComponents();

    fixture = TestBed.createComponent(LocationCreatePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
