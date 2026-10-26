import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KitchenBoardPage } from './kitchen-board-page';

describe('KitchenBoardPage', () => {
  let component: KitchenBoardPage;
  let fixture: ComponentFixture<KitchenBoardPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [KitchenBoardPage],
    }).compileComponents();

    fixture = TestBed.createComponent(KitchenBoardPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
