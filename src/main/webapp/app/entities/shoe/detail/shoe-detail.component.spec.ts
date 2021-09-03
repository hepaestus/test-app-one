import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ShoeDetailComponent } from './shoe-detail.component';

describe('Component Tests', () => {
  describe('Shoe Management Detail Component', () => {
    let comp: ShoeDetailComponent;
    let fixture: ComponentFixture<ShoeDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [ShoeDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ shoe: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(ShoeDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ShoeDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load shoe on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.shoe).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
