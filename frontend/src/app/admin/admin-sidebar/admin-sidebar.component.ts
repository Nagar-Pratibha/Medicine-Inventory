import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-admin-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-sidebar.component.html',
  // Use the same CSS file as the user sidebar for a consistent, sharp look
  styleUrls: ['../../sidebar/sidebar.component.css']
})
export class AdminSidebarComponent {
  menuItems = [
    { label: 'Medicines List', icon: '📋', link: '/admin/medicines' },
    { label: 'Manage Medicines', icon: '⚙️', link: '/admin/manage-medicines' },
    { label: 'Orders', icon: '🛒', link: '/admin/orders' }
  ];

  constructor(private authService: AuthService) {}

  logout() {
    this.authService.logout();
  }
}