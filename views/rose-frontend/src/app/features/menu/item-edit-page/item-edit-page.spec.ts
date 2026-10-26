import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemEditPage } from './item-edit-page';

describe('ItemEditPage', () => {
  let component: ItemEditPage;
  let fixture: ComponentFixture<ItemEditPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ItemEditPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ItemEditPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
