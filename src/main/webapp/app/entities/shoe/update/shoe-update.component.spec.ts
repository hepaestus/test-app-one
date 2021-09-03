jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ShoeService } from '../service/shoe.service';
import { IShoe, Shoe } from '../shoe.model';

import { ShoeUpdateComponent } from './shoe-update.component';

describe('Component Tests', () => {
  describe('Shoe Management Update Component', () => {
    let comp: ShoeUpdateComponent;
    let fixture: ComponentFixture<ShoeUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let shoeService: ShoeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ShoeUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ShoeUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ShoeUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      shoeService = TestBed.inject(ShoeService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const shoe: IShoe = { id: 456 };

        activatedRoute.data = of({ shoe });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(shoe));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Shoe>>();
        const shoe = { id: 123 };
        jest.spyOn(shoeService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ shoe });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: shoe }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(shoeService.update).toHaveBeenCalledWith(shoe);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Shoe>>();
        const shoe = new Shoe();
        jest.spyOn(shoeService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ shoe });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: shoe }));
        saveSubject.complete();

        // THEN
        expect(shoeService.create).toHaveBeenCalledWith(shoe);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Shoe>>();
        const shoe = { id: 123 };
        jest.spyOn(shoeService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ shoe });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(shoeService.update).toHaveBeenCalledWith(shoe);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
