import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthenticatedChildrenGuard } from '../shared/guards/authenticated-children.guard';
import { ManagerGuard } from '../shared/guards/manager.guard';
import { SharedModule } from '../shared/modules/shared.module';
import { ContainerComponent } from './components/container/container.component';
import { EditComponent } from './components/edit/edit.component';
import { ListComponent } from './components/list/list.component';

const routes: Routes = [
  {
    path: '',
    component: ContainerComponent,
    canActivateChild: [AuthenticatedChildrenGuard],
    children: [
      {
        path: '',
        component: ListComponent,
      },
      {
        path: 'new',
        component: EditComponent,
        canActivate: [ManagerGuard],
      },
      {
        path: 'edit/:id',
        component: EditComponent,
        canActivate: [ManagerGuard],
      },
    ],
  },
];

@NgModule({
  declarations: [ContainerComponent, EditComponent, ListComponent],
  imports: [SharedModule, RouterModule.forChild(routes)],
})
export class ProjectsModule {}
