import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseMedicinesComponent } from './browse-medicines.component';

describe('BrowseMedicinesComponent', () => {
  let component: BrowseMedicinesComponent;
  let fixture: ComponentFixture<BrowseMedicinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BrowseMedicinesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BrowseMedicinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
