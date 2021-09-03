import * as dayjs from 'dayjs';
import { IDriver } from 'app/entities/driver/driver.model';
import { IPerson } from 'app/entities/person/person.model';

export interface ICar {
  id?: number;
  make?: string | null;
  model?: string | null;
  year?: dayjs.Dayjs | null;
  driver?: IDriver;
  passengers?: IPerson[] | null;
}

export class Car implements ICar {
  constructor(
    public id?: number,
    public make?: string | null,
    public model?: string | null,
    public year?: dayjs.Dayjs | null,
    public driver?: IDriver,
    public passengers?: IPerson[] | null
  ) {}
}

export function getCarIdentifier(car: ICar): number | undefined {
  return car.id;
}
