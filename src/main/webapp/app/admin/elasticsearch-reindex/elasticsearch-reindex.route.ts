import { Route } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ElasticsearchReindexComponent } from './elasticsearch-reindex.component';

export const elasticsearchReindexRoute: Route = {
  path: 'elasticsearch-reindex',
  component: ElasticsearchReindexComponent,
  data: {
    authorities: ['ROLE_ADMIN'],
    pageTitle: 'Reindex Elasticsearch',
  },
  canActivate: [UserRouteAccessService],
};
