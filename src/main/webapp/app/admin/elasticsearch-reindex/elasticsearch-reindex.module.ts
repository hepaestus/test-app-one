import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '../../shared/shared.module';

import {
  ElasticsearchReindexComponent,
  ElasticsearchReindexModalComponent,
  ElasticsearchReindexService,
  elasticsearchReindexRoute,
} from './';
import { ElasticsearchReindexSelectedModalComponent } from './elasticsearch-reindex-selected-modal.component';

const ADMIN_ROUTES = [elasticsearchReindexRoute];

@NgModule({
  imports: [SharedModule, RouterModule.forChild(ADMIN_ROUTES)],
  declarations: [ElasticsearchReindexComponent, ElasticsearchReindexModalComponent, ElasticsearchReindexSelectedModalComponent],
  entryComponents: [ElasticsearchReindexModalComponent, ElasticsearchReindexSelectedModalComponent],
  providers: [ElasticsearchReindexService],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class testAppOneAppElasticsearchReindexModule {}
