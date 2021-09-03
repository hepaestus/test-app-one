import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IShoe } from '../shoe.model';
import { ShoeService } from '../service/shoe.service';

@Component({
  templateUrl: './shoe-delete-dialog.component.html',
})
export class ShoeDeleteDialogComponent {
  shoe?: IShoe;

  constructor(protected shoeService: ShoeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.shoeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
