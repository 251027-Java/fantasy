import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScrollableCards } from './scrollable-cards';

describe('ScrollableCards', () => {
  let component: ScrollableCards;
  let fixture: ComponentFixture<ScrollableCards>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScrollableCards]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScrollableCards);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
