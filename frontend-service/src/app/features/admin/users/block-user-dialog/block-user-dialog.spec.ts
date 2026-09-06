import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlockUserDialog } from './block-user-dialog';

describe('BlockUserDialog', () => {
  let component: BlockUserDialog;
  let fixture: ComponentFixture<BlockUserDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BlockUserDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BlockUserDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
