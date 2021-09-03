import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IShoe, Shoe } from '../shoe.model';
import { ShoeService } from '../service/shoe.service';

@Injectable({ providedIn: 'root' })
export class ShoeRoutingResolveService implements Resolve<IShoe> {
  constructor(protected service: ShoeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IShoe> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((shoe: HttpResponse<Shoe>) => {
          if (shoe.body) {
            return of(shoe.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Shoe());
  }
}
