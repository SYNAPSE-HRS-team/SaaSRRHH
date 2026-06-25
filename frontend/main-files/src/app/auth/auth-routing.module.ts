// src/app/auth/auth-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { LockScreenComponent } from './lock-screen/lock-screen.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { SignInComponent } from './sign-in/sign-in.component';
import { SignUpComponent } from './sign-up/sign-up.component';
import { SigninWithHeaderFooterComponent } from './signin-with-header-footer/signin-with-header-footer.component';
import { SignupWithHeaderFooterComponent } from './signup-with-header-footer/signup-with-header-footer.component';

const routes: Routes = [
  { path: 'sign-in', component: SignInComponent },
  { path: 'sign-up', component: SignUpComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'lock-screen', component: LockScreenComponent },
  {
    path: 'signin-with-header-footer',
    component: SigninWithHeaderFooterComponent,
  },
  {
    path: 'signup-with-header-footer',
    component: SignupWithHeaderFooterComponent,
  },
  { path: '', redirectTo: 'sign-in', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AuthRoutingModule {}
