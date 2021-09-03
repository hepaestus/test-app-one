import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPerson, Person } from '../person.model';
import { PersonService } from '../service/person.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IShoe } from 'app/entities/shoe/shoe.model';
import { ShoeService } from 'app/entities/shoe/service/shoe.service';

@Component({
  selector: 'jhi-person-update',
  templateUrl: './person-update.component.html',
})
export class PersonUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  shoesSharedCollection: IShoe[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    user: [],
    shoes: [],
  });

  constructor(
    protected personService: PersonService,
    protected userService: UserService,
    protected shoeService: ShoeService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ person }) => {
      this.updateForm(person);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const person = this.createFromForm();
    if (person.id !== undefined) {
      this.subscribeToSaveResponse(this.personService.update(person));
    } else {
      this.subscribeToSaveResponse(this.personService.create(person));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  trackShoeById(index: number, item: IShoe): number {
    return item.id!;
  }

  getSelectedShoe(option: IShoe, selectedVals?: IShoe[]): IShoe {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPerson>>): void {
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

  protected updateForm(person: IPerson): void {
    this.editForm.patchValue({
      id: person.id,
      name: person.name,
      user: person.user,
      shoes: person.shoes,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, person.user);
    this.shoesSharedCollection = this.shoeService.addShoeToCollectionIfMissing(this.shoesSharedCollection, ...(person.shoes ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.shoeService
      .query()
      .pipe(map((res: HttpResponse<IShoe[]>) => res.body ?? []))
      .pipe(map((shoes: IShoe[]) => this.shoeService.addShoeToCollectionIfMissing(shoes, ...(this.editForm.get('shoes')!.value ?? []))))
      .subscribe((shoes: IShoe[]) => (this.shoesSharedCollection = shoes));
  }

  protected createFromForm(): IPerson {
    return {
      ...new Person(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      user: this.editForm.get(['user'])!.value,
      shoes: this.editForm.get(['shoes'])!.value,
    };
  }
}
