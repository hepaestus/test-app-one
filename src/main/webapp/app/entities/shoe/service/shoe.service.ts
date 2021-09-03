import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IShoe, getShoeIdentifier } from '../shoe.model';

export type EntityResponseType = HttpResponse<IShoe>;
export type EntityArrayResponseType = HttpResponse<IShoe[]>;

@Injectable({ providedIn: 'root' })
export class ShoeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/shoes');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/shoes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(shoe: IShoe): Observable<EntityResponseType> {
    return this.http.post<IShoe>(this.resourceUrl, shoe, { observe: 'response' });
  }

  update(shoe: IShoe): Observable<EntityResponseType> {
    return this.http.put<IShoe>(`${this.resourceUrl}/${getShoeIdentifier(shoe) as number}`, shoe, { observe: 'response' });
  }

  partialUpdate(shoe: IShoe): Observable<EntityResponseType> {
    return this.http.patch<IShoe>(`${this.resourceUrl}/${getShoeIdentifier(shoe) as number}`, shoe, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IShoe>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IShoe[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IShoe[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addShoeToCollectionIfMissing(shoeCollection: IShoe[], ...shoesToCheck: (IShoe | null | undefined)[]): IShoe[] {
    const shoes: IShoe[] = shoesToCheck.filter(isPresent);
    if (shoes.length > 0) {
      const shoeCollectionIdentifiers = shoeCollection.map(shoeItem => getShoeIdentifier(shoeItem)!);
      const shoesToAdd = shoes.filter(shoeItem => {
        const shoeIdentifier = getShoeIdentifier(shoeItem);
        if (shoeIdentifier == null || shoeCollectionIdentifiers.includes(shoeIdentifier)) {
          return false;
        }
        shoeCollectionIdentifiers.push(shoeIdentifier);
        return true;
      });
      return [...shoesToAdd, ...shoeCollection];
    }
    return shoeCollection;
  }
}
