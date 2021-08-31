import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DriverDetailComponent } from './driver-detail.component';

describe('Component Tests', () => {
  describe('Driver Management Detail Component', () => {
    let comp: DriverDetailComponent;
    let fixture: ComponentFixture<DriverDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [DriverDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ driver: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(DriverDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(DriverDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load driver on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.driver).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
