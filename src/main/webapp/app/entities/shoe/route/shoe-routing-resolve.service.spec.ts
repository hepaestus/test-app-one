jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IShoe, Shoe } from '../shoe.model';
import { ShoeService } from '../service/shoe.service';

import { ShoeRoutingResolveService } from './shoe-routing-resolve.service';

describe('Service Tests', () => {
  describe('Shoe routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: ShoeRoutingResolveService;
    let service: ShoeService;
    let resultShoe: IShoe | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(ShoeRoutingResolveService);
      service = TestBed.inject(ShoeService);
      resultShoe = undefined;
    });

    describe('resolve', () => {
      it('should return IShoe returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultShoe = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultShoe).toEqual({ id: 123 });
      });

      it('should return new IShoe if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultShoe = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultShoe).toEqual(new Shoe());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Shoe })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultShoe = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultShoe).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
