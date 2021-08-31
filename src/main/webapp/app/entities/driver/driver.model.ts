import { IPerson } from 'app/entities/person/person.model';

export interface IDriver {
  id?: number;
  licenseNumber?: string | null;
  person?: IPerson | null;
}

export class Driver implements IDriver {
  constructor(public id?: number, public licenseNumber?: string | null, public person?: IPerson | null) {}
}

export function getDriverIdentifier(driver: IDriver): number | undefined {
  return driver.id;
}
