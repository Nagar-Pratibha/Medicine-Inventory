import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../auth.service';
import { CartService } from '../services/cart.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  cartItemCount = 0;
  menuItems = [
    { label: 'Browse Medicines', icon: '💊', link: '/browse-medicines' },
    { label: 'Cart', icon: '🛍️', link: '/cart' },
    { label: 'Order History', icon: '📜', link: '/orders' },
    { label: 'My Profile', icon: '👤', link: '/my-profile' },
    { label: 'Feedback', icon: '💬', link: '/feedback' },
    { label: 'About Us', icon: '🏢', link: '/about-us' },
    { label: 'Contact Us', icon: '📞', link: '/contact-us' },
  ];

  constructor(
    private authService: AuthService,
    public cartService: CartService
  ) {}

  ngOnInit() {
    this.cartService.currentCart.subscribe(items => {
      this.cartItemCount = items.reduce((total, item) => total + item.quantity, 0);
    });
  }

  logout(): void { this.authService.logout(); }
}