export interface IDriver {
  id?: number;
  licenseNumber?: string | null;
}

export class Driver implements IDriver {
  constructor(public id?: number, public licenseNumber?: string | null) {}
}

export function getDriverIdentifier(driver: IDriver): number | undefined {
  return driver.id;
}
