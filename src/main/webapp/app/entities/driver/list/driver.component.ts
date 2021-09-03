import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IDriver } from '../driver.model';
import { DriverService } from '../service/driver.service';
import { DriverDeleteDialogComponent } from '../delete/driver-delete-dialog.component';

@Component({
  selector: 'jhi-driver',
  templateUrl: './driver.component.html',
})
export class DriverComponent implements OnInit {
  drivers?: IDriver[];
  isLoading = false;
  currentSearch: string;

  constructor(protected driverService: DriverService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.driverService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IDriver[]>) => {
            this.isLoading = false;
            this.drivers = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.driverService.query().subscribe(
      (res: HttpResponse<IDriver[]>) => {
        this.isLoading = false;
        this.drivers = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IDriver): number {
    return item.id!;
  }

  delete(driver: IDriver): void {
    const modalRef = this.modalService.open(DriverDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.driver = driver;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
