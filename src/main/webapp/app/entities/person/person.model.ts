import { IUser } from 'app/entities/user/user.model';
import { IShoe } from 'app/entities/shoe/shoe.model';
import { IDriver } from 'app/entities/driver/driver.model';
import { ICar } from 'app/entities/car/car.model';

export interface IPerson {
  id?: number;
  name?: string | null;
  user?: IUser | null;
  shoes?: IShoe[] | null;
  driver?: IDriver | null;
  cars?: ICar[] | null;
}

export class Person implements IPerson {
  constructor(
    public id?: number,
    public name?: string | null,
    public user?: IUser | null,
    public shoes?: IShoe[] | null,
    public driver?: IDriver | null,
    public cars?: ICar[] | null
  ) {}
}

export function getPersonIdentifier(person: IPerson): number | undefined {
  return person.id;
}
