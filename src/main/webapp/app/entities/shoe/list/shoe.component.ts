import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IShoe } from '../shoe.model';
import { ShoeService } from '../service/shoe.service';
import { ShoeDeleteDialogComponent } from '../delete/shoe-delete-dialog.component';

@Component({
  selector: 'jhi-shoe',
  templateUrl: './shoe.component.html',
})
export class ShoeComponent implements OnInit {
  shoes?: IShoe[];
  isLoading = false;
  currentSearch: string;

  constructor(protected shoeService: ShoeService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.shoeService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IShoe[]>) => {
            this.isLoading = false;
            this.shoes = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.shoeService.query().subscribe(
      (res: HttpResponse<IShoe[]>) => {
        this.isLoading = false;
        this.shoes = res.body ?? [];
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

  trackId(index: number, item: IShoe): number {
    return item.id!;
  }

  delete(shoe: IShoe): void {
    const modalRef = this.modalService.open(ShoeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.shoe = shoe;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
