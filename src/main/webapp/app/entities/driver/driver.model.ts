import { IPerson } from 'app/entities/person/person.model';
import { ICar } from 'app/entities/car/car.model';

export interface IDriver {
  id?: number;
  licenseNumber?: string;
  person?: IPerson;
  cars?: ICar[];
}

export class Driver implements IDriver {
  constructor(public id?: number, public licenseNumber?: string, public person?: IPerson, public cars?: ICar[]) {}
}

export function getDriverIdentifier(driver: IDriver): number | undefined {
  return driver.id;
}
