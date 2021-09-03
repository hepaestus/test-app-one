import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ShoeComponent } from '../list/shoe.component';
import { ShoeDetailComponent } from '../detail/shoe-detail.component';
import { ShoeUpdateComponent } from '../update/shoe-update.component';
import { ShoeRoutingResolveService } from './shoe-routing-resolve.service';

const shoeRoute: Routes = [
  {
    path: '',
    component: ShoeComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ShoeDetailComponent,
    resolve: {
      shoe: ShoeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ShoeUpdateComponent,
    resolve: {
      shoe: ShoeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ShoeUpdateComponent,
    resolve: {
      shoe: ShoeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(shoeRoute)],
  exports: [RouterModule],
})
export class ShoeRoutingModule {}
