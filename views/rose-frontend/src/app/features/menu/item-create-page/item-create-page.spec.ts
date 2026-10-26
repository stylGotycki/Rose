import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemCreatePage } from './item-create-page';

describe('ItemCreatePage', () => {
  let component: ItemCreatePage;
  let fixture: ComponentFixture<ItemCreatePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ItemCreatePage],
    }).compileComponents();

    fixture = TestBed.createComponent(ItemCreatePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
