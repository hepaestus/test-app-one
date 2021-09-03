import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IShoe, Shoe } from '../shoe.model';

import { ShoeService } from './shoe.service';

describe('Service Tests', () => {
  describe('Shoe Service', () => {
    let service: ShoeService;
    let httpMock: HttpTestingController;
    let elemDefault: IShoe;
    let expectedResult: IShoe | IShoe[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(ShoeService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        shoeSize: 0,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Shoe', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Shoe()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Shoe', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            shoeSize: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Shoe', () => {
        const patchObject = Object.assign(
          {
            shoeSize: 1,
          },
          new Shoe()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Shoe', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            shoeSize: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Shoe', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addShoeToCollectionIfMissing', () => {
        it('should add a Shoe to an empty array', () => {
          const shoe: IShoe = { id: 123 };
          expectedResult = service.addShoeToCollectionIfMissing([], shoe);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(shoe);
        });

        it('should not add a Shoe to an array that contains it', () => {
          const shoe: IShoe = { id: 123 };
          const shoeCollection: IShoe[] = [
            {
              ...shoe,
            },
            { id: 456 },
          ];
          expectedResult = service.addShoeToCollectionIfMissing(shoeCollection, shoe);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Shoe to an array that doesn't contain it", () => {
          const shoe: IShoe = { id: 123 };
          const shoeCollection: IShoe[] = [{ id: 456 }];
          expectedResult = service.addShoeToCollectionIfMissing(shoeCollection, shoe);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(shoe);
        });

        it('should add only unique Shoe to an array', () => {
          const shoeArray: IShoe[] = [{ id: 123 }, { id: 456 }, { id: 83264 }];
          const shoeCollection: IShoe[] = [{ id: 123 }];
          expectedResult = service.addShoeToCollectionIfMissing(shoeCollection, ...shoeArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const shoe: IShoe = { id: 123 };
          const shoe2: IShoe = { id: 456 };
          expectedResult = service.addShoeToCollectionIfMissing([], shoe, shoe2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(shoe);
          expect(expectedResult).toContain(shoe2);
        });

        it('should accept null and undefined values', () => {
          const shoe: IShoe = { id: 123 };
          expectedResult = service.addShoeToCollectionIfMissing([], null, shoe, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(shoe);
        });

        it('should return initial array if no Shoe is added', () => {
          const shoeCollection: IShoe[] = [{ id: 123 }];
          expectedResult = service.addShoeToCollectionIfMissing(shoeCollection, undefined, null);
          expect(expectedResult).toEqual(shoeCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
