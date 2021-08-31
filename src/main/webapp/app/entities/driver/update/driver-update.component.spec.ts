jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { DriverService } from '../service/driver.service';
import { IDriver, Driver } from '../driver.model';
import { IPerson } from 'app/entities/person/person.model';
import { PersonService } from 'app/entities/person/service/person.service';

import { DriverUpdateComponent } from './driver-update.component';

describe('Component Tests', () => {
  describe('Driver Management Update Component', () => {
    let comp: DriverUpdateComponent;
    let fixture: ComponentFixture<DriverUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let driverService: DriverService;
    let personService: PersonService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [DriverUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(DriverUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(DriverUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      driverService = TestBed.inject(DriverService);
      personService = TestBed.inject(PersonService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call person query and add missing value', () => {
        const driver: IDriver = { id: 456 };
        const person: IPerson = { id: 22415 };
        driver.person = person;

        const personCollection: IPerson[] = [{ id: 46976 }];
        jest.spyOn(personService, 'query').mockReturnValue(of(new HttpResponse({ body: personCollection })));
        const expectedCollection: IPerson[] = [person, ...personCollection];
        jest.spyOn(personService, 'addPersonToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ driver });
        comp.ngOnInit();

        expect(personService.query).toHaveBeenCalled();
        expect(personService.addPersonToCollectionIfMissing).toHaveBeenCalledWith(personCollection, person);
        expect(comp.peopleCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const driver: IDriver = { id: 456 };
        const person: IPerson = { id: 4175 };
        driver.person = person;

        activatedRoute.data = of({ driver });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(driver));
        expect(comp.peopleCollection).toContain(person);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Driver>>();
        const driver = { id: 123 };
        jest.spyOn(driverService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ driver });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: driver }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(driverService.update).toHaveBeenCalledWith(driver);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Driver>>();
        const driver = new Driver();
        jest.spyOn(driverService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ driver });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: driver }));
        saveSubject.complete();

        // THEN
        expect(driverService.create).toHaveBeenCalledWith(driver);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Driver>>();
        const driver = { id: 123 };
        jest.spyOn(driverService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ driver });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(driverService.update).toHaveBeenCalledWith(driver);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackPersonById', () => {
        it('Should return tracked Person primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackPersonById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
