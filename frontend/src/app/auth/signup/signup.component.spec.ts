import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './signup.component.html',
  styleUrls: [] // Reusing styles
})
export class SignUpComponent {
  selectedRole: 'admin' | 'user' = 'user';
  constructor(private authService: AuthService) {}
  onSubmit() {
    this.authService.login(this.selectedRole);
  }
}