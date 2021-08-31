import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDriver, getDriverIdentifier } from '../driver.model';

export type EntityResponseType = HttpResponse<IDriver>;
export type EntityArrayResponseType = HttpResponse<IDriver[]>;

@Injectable({ providedIn: 'root' })
export class DriverService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/drivers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(driver: IDriver): Observable<EntityResponseType> {
    return this.http.post<IDriver>(this.resourceUrl, driver, { observe: 'response' });
  }

  update(driver: IDriver): Observable<EntityResponseType> {
    return this.http.put<IDriver>(`${this.resourceUrl}/${getDriverIdentifier(driver) as number}`, driver, { observe: 'response' });
  }

  partialUpdate(driver: IDriver): Observable<EntityResponseType> {
    return this.http.patch<IDriver>(`${this.resourceUrl}/${getDriverIdentifier(driver) as number}`, driver, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDriver>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDriver[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addDriverToCollectionIfMissing(driverCollection: IDriver[], ...driversToCheck: (IDriver | null | undefined)[]): IDriver[] {
    const drivers: IDriver[] = driversToCheck.filter(isPresent);
    if (drivers.length > 0) {
      const driverCollectionIdentifiers = driverCollection.map(driverItem => getDriverIdentifier(driverItem)!);
      const driversToAdd = drivers.filter(driverItem => {
        const driverIdentifier = getDriverIdentifier(driverItem);
        if (driverIdentifier == null || driverCollectionIdentifiers.includes(driverIdentifier)) {
          return false;
        }
        driverCollectionIdentifiers.push(driverIdentifier);
        return true;
      });
      return [...driversToAdd, ...driverCollection];
    }
    return driverCollection;
  }
}
