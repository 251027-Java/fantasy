import { Component, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HlmFormField, HlmHint, HlmError, HlmFormFieldImports } from "@spartan-ng/helm/form-field";
import { HlmInputImports } from '@spartan-ng/helm/input';


@Component({
  selector: 'app-login',
  imports: [HlmInputImports, HlmFormFieldImports,  ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {

  public loginControl = new FormControl('', [Validators.required, Validators.email]);
  constructor(private router:Router){}


  ngOnInit(): void {
    this.loginControl.valueChanges.subscribe(value => {
      console.log('Login input changed to:', value);
    });
  }

  Login(): void {
    if (this.loginControl.valid) {
      // Perform login logic here
      console.log('Logging in with:', this.loginControl.value);
      this.router.navigateByUrl('league');
    } else {
      console.log('Login form is invalid');
    }
  }
}
