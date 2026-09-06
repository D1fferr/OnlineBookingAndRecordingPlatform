import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CancelDialog } from './cancel-dialog';

describe('CancelDialog', () => {
  let component: CancelDialog;
  let fixture: ComponentFixture<CancelDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CancelDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CancelDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
