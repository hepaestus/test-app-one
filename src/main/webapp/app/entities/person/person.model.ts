import { ICar } from 'app/entities/car/car.model';

export interface IPerson {
  id?: number;
  name?: string | null;
  car?: ICar | null;
}

export class Person implements IPerson {
  constructor(public id?: number, public name?: string | null, public car?: ICar | null) {}
}

export function getPersonIdentifier(person: IPerson): number | undefined {
  return person.id;
}
