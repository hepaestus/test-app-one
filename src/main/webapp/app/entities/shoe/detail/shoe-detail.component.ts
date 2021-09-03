import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IShoe } from '../shoe.model';

@Component({
  selector: 'jhi-shoe-detail',
  templateUrl: './shoe-detail.component.html',
})
export class ShoeDetailComponent implements OnInit {
  shoe: IShoe | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ shoe }) => {
      this.shoe = shoe;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
