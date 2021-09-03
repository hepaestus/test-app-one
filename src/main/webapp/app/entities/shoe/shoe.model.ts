import { IPerson } from 'app/entities/person/person.model';

export interface IShoe {
  id?: number;
  shoeSize?: number | null;
  people?: IPerson[] | null;
}

export class Shoe implements IShoe {
  constructor(public id?: number, public shoeSize?: number | null, public people?: IPerson[] | null) {}
}

export function getShoeIdentifier(shoe: IShoe): number | undefined {
  return shoe.id;
}
