import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AssociateGuard } from '../shared/guards/associate.guard';
import { AuthenticatedChildrenGuard } from '../shared/guards/authenticated-children.guard';
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
        canActivate: [AssociateGuard],
      },
      {
        path: 'edit/:id',
        component: EditComponent,
        canActivate: [AssociateGuard],
      },
    ],
  },
];

@NgModule({
  declarations: [ContainerComponent, EditComponent, ListComponent],
  imports: [SharedModule, RouterModule.forChild(routes)],
})
export class TasksModule {}
