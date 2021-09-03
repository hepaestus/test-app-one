import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ICar, getCarIdentifier } from '../car.model';

export type EntityResponseType = HttpResponse<ICar>;
export type EntityArrayResponseType = HttpResponse<ICar[]>;

@Injectable({ providedIn: 'root' })
export class CarService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cars');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/cars');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(car: ICar): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(car);
    return this.http
      .post<ICar>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(car: ICar): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(car);
    return this.http
      .put<ICar>(`${this.resourceUrl}/${getCarIdentifier(car) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(car: ICar): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(car);
    return this.http
      .patch<ICar>(`${this.resourceUrl}/${getCarIdentifier(car) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ICar>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICar[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICar[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addCarToCollectionIfMissing(carCollection: ICar[], ...carsToCheck: (ICar | null | undefined)[]): ICar[] {
    const cars: ICar[] = carsToCheck.filter(isPresent);
    if (cars.length > 0) {
      const carCollectionIdentifiers = carCollection.map(carItem => getCarIdentifier(carItem)!);
      const carsToAdd = cars.filter(carItem => {
        const carIdentifier = getCarIdentifier(carItem);
        if (carIdentifier == null || carCollectionIdentifiers.includes(carIdentifier)) {
          return false;
        }
        carCollectionIdentifiers.push(carIdentifier);
        return true;
      });
      return [...carsToAdd, ...carCollection];
    }
    return carCollection;
  }

  protected convertDateFromClient(car: ICar): ICar {
    return Object.assign({}, car, {
      year: car.year?.isValid() ? car.year.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.year = res.body.year ? dayjs(res.body.year) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((car: ICar) => {
        car.year = car.year ? dayjs(car.year) : undefined;
      });
    }
    return res;
  }
}
