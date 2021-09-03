import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ShoeComponent } from './list/shoe.component';
import { ShoeDetailComponent } from './detail/shoe-detail.component';
import { ShoeUpdateComponent } from './update/shoe-update.component';
import { ShoeDeleteDialogComponent } from './delete/shoe-delete-dialog.component';
import { ShoeRoutingModule } from './route/shoe-routing.module';

@NgModule({
  imports: [SharedModule, ShoeRoutingModule],
  declarations: [ShoeComponent, ShoeDetailComponent, ShoeUpdateComponent, ShoeDeleteDialogComponent],
  entryComponents: [ShoeDeleteDialogComponent],
})
export class ShoeModule {}
