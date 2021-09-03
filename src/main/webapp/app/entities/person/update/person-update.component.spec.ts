jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { PersonService } from '../service/person.service';
import { IPerson, Person } from '../person.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IShoe } from 'app/entities/shoe/shoe.model';
import { ShoeService } from 'app/entities/shoe/service/shoe.service';

import { PersonUpdateComponent } from './person-update.component';

describe('Component Tests', () => {
  describe('Person Management Update Component', () => {
    let comp: PersonUpdateComponent;
    let fixture: ComponentFixture<PersonUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let personService: PersonService;
    let userService: UserService;
    let shoeService: ShoeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PersonUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(PersonUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PersonUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      personService = TestBed.inject(PersonService);
      userService = TestBed.inject(UserService);
      shoeService = TestBed.inject(ShoeService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call User query and add missing value', () => {
        const person: IPerson = { id: 456 };
        const user: IUser = { id: 57046 };
        person.user = user;

        const userCollection: IUser[] = [{ id: 27076 }];
        jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [user];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ person });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Shoe query and add missing value', () => {
        const person: IPerson = { id: 456 };
        const shoes: IShoe[] = [{ id: 7990 }];
        person.shoes = shoes;

        const shoeCollection: IShoe[] = [{ id: 11729 }];
        jest.spyOn(shoeService, 'query').mockReturnValue(of(new HttpResponse({ body: shoeCollection })));
        const additionalShoes = [...shoes];
        const expectedCollection: IShoe[] = [...additionalShoes, ...shoeCollection];
        jest.spyOn(shoeService, 'addShoeToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ person });
        comp.ngOnInit();

        expect(shoeService.query).toHaveBeenCalled();
        expect(shoeService.addShoeToCollectionIfMissing).toHaveBeenCalledWith(shoeCollection, ...additionalShoes);
        expect(comp.shoesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const person: IPerson = { id: 456 };
        const user: IUser = { id: 40784 };
        person.user = user;
        const shoes: IShoe = { id: 7593 };
        person.shoes = [shoes];

        activatedRoute.data = of({ person });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(person));
        expect(comp.usersSharedCollection).toContain(user);
        expect(comp.shoesSharedCollection).toContain(shoes);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Person>>();
        const person = { id: 123 };
        jest.spyOn(personService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ person });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: person }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(personService.update).toHaveBeenCalledWith(person);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Person>>();
        const person = new Person();
        jest.spyOn(personService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ person });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: person }));
        saveSubject.complete();

        // THEN
        expect(personService.create).toHaveBeenCalledWith(person);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Person>>();
        const person = { id: 123 };
        jest.spyOn(personService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ person });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(personService.update).toHaveBeenCalledWith(person);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackUserById', () => {
        it('Should return tracked User primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackUserById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackShoeById', () => {
        it('Should return tracked Shoe primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackShoeById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });

    describe('Getting selected relationships', () => {
      describe('getSelectedShoe', () => {
        it('Should return option if no Shoe is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedShoe(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected Shoe for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedShoe(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this Shoe is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedShoe(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });
    });
  });
});
