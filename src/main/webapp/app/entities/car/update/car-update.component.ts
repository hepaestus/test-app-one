import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICar, Car } from '../car.model';
import { CarService } from '../service/car.service';
import { IDriver } from 'app/entities/driver/driver.model';
import { DriverService } from 'app/entities/driver/service/driver.service';
import { IPerson } from 'app/entities/person/person.model';
import { PersonService } from 'app/entities/person/service/person.service';

@Component({
  selector: 'jhi-car-update',
  templateUrl: './car-update.component.html',
})
export class CarUpdateComponent implements OnInit {
  isSaving = false;

  driversSharedCollection: IDriver[] = [];
  peopleSharedCollection: IPerson[] = [];

  editForm = this.fb.group({
    id: [],
    make: [],
    model: [],
    year: [],
    driver: [null, Validators.required],
    passengers: [],
  });

  constructor(
    protected carService: CarService,
    protected driverService: DriverService,
    protected personService: PersonService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ car }) => {
      this.updateForm(car);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const car = this.createFromForm();
    if (car.id !== undefined) {
      this.subscribeToSaveResponse(this.carService.update(car));
    } else {
      this.subscribeToSaveResponse(this.carService.create(car));
    }
  }

  trackDriverById(index: number, item: IDriver): number {
    return item.id!;
  }

  trackPersonById(index: number, item: IPerson): number {
    return item.id!;
  }

  getSelectedPerson(option: IPerson, selectedVals?: IPerson[]): IPerson {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICar>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(car: ICar): void {
    this.editForm.patchValue({
      id: car.id,
      make: car.make,
      model: car.model,
      year: car.year,
      driver: car.driver,
      passengers: car.passengers,
    });

    this.driversSharedCollection = this.driverService.addDriverToCollectionIfMissing(this.driversSharedCollection, car.driver);
    this.peopleSharedCollection = this.personService.addPersonToCollectionIfMissing(this.peopleSharedCollection, ...(car.passengers ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.driverService
      .query()
      .pipe(map((res: HttpResponse<IDriver[]>) => res.body ?? []))
      .pipe(map((drivers: IDriver[]) => this.driverService.addDriverToCollectionIfMissing(drivers, this.editForm.get('driver')!.value)))
      .subscribe((drivers: IDriver[]) => (this.driversSharedCollection = drivers));

    this.personService
      .query()
      .pipe(map((res: HttpResponse<IPerson[]>) => res.body ?? []))
      .pipe(
        map((people: IPerson[]) =>
          this.personService.addPersonToCollectionIfMissing(people, ...(this.editForm.get('passengers')!.value ?? []))
        )
      )
      .subscribe((people: IPerson[]) => (this.peopleSharedCollection = people));
  }

  protected createFromForm(): ICar {
    return {
      ...new Car(),
      id: this.editForm.get(['id'])!.value,
      make: this.editForm.get(['make'])!.value,
      model: this.editForm.get(['model'])!.value,
      year: this.editForm.get(['year'])!.value,
      driver: this.editForm.get(['driver'])!.value,
      passengers: this.editForm.get(['passengers'])!.value,
    };
  }
}
