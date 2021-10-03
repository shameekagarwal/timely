import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NotAuthenticatedGuard } from '../shared/guards/not-authenticated.guard';
import { SharedModule } from '../shared/modules/shared.module';
import { ContainerComponent } from './components/container/container.component';
import { SigninComponent } from './components/signin/signin.component';
import { SignupComponent } from './components/signup/signup.component';

const routes: Routes = [
  {
    path: '',
    component: ContainerComponent,
    canActivate: [NotAuthenticatedGuard],
  },
];

@NgModule({
  declarations: [ContainerComponent, SigninComponent, SignupComponent],
  imports: [SharedModule, RouterModule.forChild(routes)],
})
export class AuthModule {}
