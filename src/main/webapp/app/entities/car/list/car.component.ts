import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICar } from '../car.model';
import { CarService } from '../service/car.service';
import { CarDeleteDialogComponent } from '../delete/car-delete-dialog.component';

@Component({
  selector: 'jhi-car',
  templateUrl: './car.component.html',
})
export class CarComponent implements OnInit {
  cars?: ICar[];
  isLoading = false;

  constructor(protected carService: CarService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.carService.query().subscribe(
      (res: HttpResponse<ICar[]>) => {
        this.isLoading = false;
        this.cars = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ICar): number {
    return item.id!;
  }

  delete(car: ICar): void {
    const modalRef = this.modalService.open(CarDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.car = car;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
