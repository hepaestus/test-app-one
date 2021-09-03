import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPerson } from '../person.model';
import { PersonService } from '../service/person.service';
import { PersonDeleteDialogComponent } from '../delete/person-delete-dialog.component';

@Component({
  selector: 'jhi-person',
  templateUrl: './person.component.html',
})
export class PersonComponent implements OnInit {
  people?: IPerson[];
  isLoading = false;
  currentSearch: string;

  constructor(protected personService: PersonService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.personService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IPerson[]>) => {
            this.isLoading = false;
            this.people = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.personService.query().subscribe(
      (res: HttpResponse<IPerson[]>) => {
        this.isLoading = false;
        this.people = res.body ?? [];
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

  trackId(index: number, item: IPerson): number {
    return item.id!;
  }

  delete(person: IPerson): void {
    const modalRef = this.modalService.open(PersonDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.person = person;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
