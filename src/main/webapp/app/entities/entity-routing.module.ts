import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'car',
        data: { pageTitle: 'Cars' },
        loadChildren: () => import('./car/car.module').then(m => m.CarModule),
      },
      {
        path: 'person',
        data: { pageTitle: 'People' },
        loadChildren: () => import('./person/person.module').then(m => m.PersonModule),
      },
      {
        path: 'shoe',
        data: { pageTitle: 'Shoes' },
        loadChildren: () => import('./shoe/shoe.module').then(m => m.ShoeModule),
      },
      {
        path: 'driver',
        data: { pageTitle: 'Drivers' },
        loadChildren: () => import('./driver/driver.module').then(m => m.DriverModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
