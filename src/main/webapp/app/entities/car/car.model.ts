import { IDriver } from 'app/entities/driver/driver.model';
import { IPerson } from 'app/entities/person/person.model';

export interface ICar {
  id?: number;
  make?: string | null;
  model?: string | null;
  driver?: IDriver | null;
  passengers?: IPerson[] | null;
}

export class Car implements ICar {
  constructor(
    public id?: number,
    public make?: string | null,
    public model?: string | null,
    public driver?: IDriver | null,
    public passengers?: IPerson[] | null
  ) {}
}

export function getCarIdentifier(car: ICar): number | undefined {
  return car.id;
}
