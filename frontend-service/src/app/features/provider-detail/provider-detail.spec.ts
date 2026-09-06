import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProviderDetail } from './provider-detail';

describe('ProviderDetail', () => {
  let component: ProviderDetail;
  let fixture: ComponentFixture<ProviderDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProviderDetail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProviderDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
