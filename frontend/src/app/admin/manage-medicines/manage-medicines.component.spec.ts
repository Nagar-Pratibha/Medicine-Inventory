import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageMedicinesComponent } from './manage-medicines.component';

describe('ManageMedicinesComponent', () => {
  let component: ManageMedicinesComponent;
  let fixture: ComponentFixture<ManageMedicinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageMedicinesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageMedicinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
