import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IShoe, Shoe } from '../shoe.model';
import { ShoeService } from '../service/shoe.service';

@Component({
  selector: 'jhi-shoe-update',
  templateUrl: './shoe-update.component.html',
})
export class ShoeUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    shoeSize: [null, [Validators.min(4), Validators.max(18)]],
  });

  constructor(protected shoeService: ShoeService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ shoe }) => {
      this.updateForm(shoe);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const shoe = this.createFromForm();
    if (shoe.id !== undefined) {
      this.subscribeToSaveResponse(this.shoeService.update(shoe));
    } else {
      this.subscribeToSaveResponse(this.shoeService.create(shoe));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IShoe>>): void {
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

  protected updateForm(shoe: IShoe): void {
    this.editForm.patchValue({
      id: shoe.id,
      shoeSize: shoe.shoeSize,
    });
  }

  protected createFromForm(): IShoe {
    return {
      ...new Shoe(),
      id: this.editForm.get(['id'])!.value,
      shoeSize: this.editForm.get(['shoeSize'])!.value,
    };
  }
}
