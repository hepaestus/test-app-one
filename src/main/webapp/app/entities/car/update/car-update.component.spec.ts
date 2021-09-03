jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { CarService } from '../service/car.service';
import { ICar, Car } from '../car.model';
import { IDriver } from 'app/entities/driver/driver.model';
import { DriverService } from 'app/entities/driver/service/driver.service';
import { IPerson } from 'app/entities/person/person.model';
import { PersonService } from 'app/entities/person/service/person.service';

import { CarUpdateComponent } from './car-update.component';

describe('Component Tests', () => {
  describe('Car Management Update Component', () => {
    let comp: CarUpdateComponent;
    let fixture: ComponentFixture<CarUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let carService: CarService;
    let driverService: DriverService;
    let personService: PersonService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CarUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(CarUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CarUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      carService = TestBed.inject(CarService);
      driverService = TestBed.inject(DriverService);
      personService = TestBed.inject(PersonService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Driver query and add missing value', () => {
        const car: ICar = { id: 456 };
        const driver: IDriver = { id: 17292 };
        car.driver = driver;

        const driverCollection: IDriver[] = [{ id: 62558 }];
        jest.spyOn(driverService, 'query').mockReturnValue(of(new HttpResponse({ body: driverCollection })));
        const additionalDrivers = [driver];
        const expectedCollection: IDriver[] = [...additionalDrivers, ...driverCollection];
        jest.spyOn(driverService, 'addDriverToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ car });
        comp.ngOnInit();

        expect(driverService.query).toHaveBeenCalled();
        expect(driverService.addDriverToCollectionIfMissing).toHaveBeenCalledWith(driverCollection, ...additionalDrivers);
        expect(comp.driversSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Person query and add missing value', () => {
        const car: ICar = { id: 456 };
        const passengers: IPerson[] = [{ id: 30996 }];
        car.passengers = passengers;

        const personCollection: IPerson[] = [{ id: 14898 }];
        jest.spyOn(personService, 'query').mockReturnValue(of(new HttpResponse({ body: personCollection })));
        const additionalPeople = [...passengers];
        const expectedCollection: IPerson[] = [...additionalPeople, ...personCollection];
        jest.spyOn(personService, 'addPersonToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ car });
        comp.ngOnInit();

        expect(personService.query).toHaveBeenCalled();
        expect(personService.addPersonToCollectionIfMissing).toHaveBeenCalledWith(personCollection, ...additionalPeople);
        expect(comp.peopleSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const car: ICar = { id: 456 };
        const driver: IDriver = { id: 93544 };
        car.driver = driver;
        const passengers: IPerson = { id: 30993 };
        car.passengers = [passengers];

        activatedRoute.data = of({ car });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(car));
        expect(comp.driversSharedCollection).toContain(driver);
        expect(comp.peopleSharedCollection).toContain(passengers);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Car>>();
        const car = { id: 123 };
        jest.spyOn(carService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ car });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: car }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(carService.update).toHaveBeenCalledWith(car);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Car>>();
        const car = new Car();
        jest.spyOn(carService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ car });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: car }));
        saveSubject.complete();

        // THEN
        expect(carService.create).toHaveBeenCalledWith(car);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Car>>();
        const car = { id: 123 };
        jest.spyOn(carService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ car });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(carService.update).toHaveBeenCalledWith(car);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackDriverById', () => {
        it('Should return tracked Driver primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackDriverById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackPersonById', () => {
        it('Should return tracked Person primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackPersonById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });

    describe('Getting selected relationships', () => {
      describe('getSelectedPerson', () => {
        it('Should return option if no Person is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedPerson(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected Person for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedPerson(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this Person is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedPerson(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });
    });
  });
});
