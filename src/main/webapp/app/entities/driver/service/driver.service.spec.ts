import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDriver, Driver } from '../driver.model';

import { DriverService } from './driver.service';

describe('Service Tests', () => {
  describe('Driver Service', () => {
    let service: DriverService;
    let httpMock: HttpTestingController;
    let elemDefault: IDriver;
    let expectedResult: IDriver | IDriver[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(DriverService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        licenseNumber: 'AAAAAAA',
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

      it('should create a Driver', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Driver()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Driver', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            licenseNumber: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Driver', () => {
        const patchObject = Object.assign({}, new Driver());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Driver', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            licenseNumber: 'BBBBBB',
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

      it('should delete a Driver', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addDriverToCollectionIfMissing', () => {
        it('should add a Driver to an empty array', () => {
          const driver: IDriver = { id: 123 };
          expectedResult = service.addDriverToCollectionIfMissing([], driver);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(driver);
        });

        it('should not add a Driver to an array that contains it', () => {
          const driver: IDriver = { id: 123 };
          const driverCollection: IDriver[] = [
            {
              ...driver,
            },
            { id: 456 },
          ];
          expectedResult = service.addDriverToCollectionIfMissing(driverCollection, driver);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Driver to an array that doesn't contain it", () => {
          const driver: IDriver = { id: 123 };
          const driverCollection: IDriver[] = [{ id: 456 }];
          expectedResult = service.addDriverToCollectionIfMissing(driverCollection, driver);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(driver);
        });

        it('should add only unique Driver to an array', () => {
          const driverArray: IDriver[] = [{ id: 123 }, { id: 456 }, { id: 40520 }];
          const driverCollection: IDriver[] = [{ id: 123 }];
          expectedResult = service.addDriverToCollectionIfMissing(driverCollection, ...driverArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const driver: IDriver = { id: 123 };
          const driver2: IDriver = { id: 456 };
          expectedResult = service.addDriverToCollectionIfMissing([], driver, driver2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(driver);
          expect(expectedResult).toContain(driver2);
        });

        it('should accept null and undefined values', () => {
          const driver: IDriver = { id: 123 };
          expectedResult = service.addDriverToCollectionIfMissing([], null, driver, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(driver);
        });

        it('should return initial array if no Driver is added', () => {
          const driverCollection: IDriver[] = [{ id: 123 }];
          expectedResult = service.addDriverToCollectionIfMissing(driverCollection, undefined, null);
          expect(expectedResult).toEqual(driverCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
