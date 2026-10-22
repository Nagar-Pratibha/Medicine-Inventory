import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { environment } from '../../environments/environment.development';

interface Address {
  addressId: number;
  line1: string;
  line2: string | null;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  default: boolean;
}

interface OrderItem {
  orderItemId: number;
  medicineName: string;
  quantity: number;
  priceAtPurchase: number | null;
}

export interface Order {
  orderId: number;
  userId: number;
  address: Address;
  totalAmount: number;
  status: string;
  createdAt: string;
  items: OrderItem[];
}

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './order-history.component.html',
  styleUrls: ['./order-history.component.css']
})
export class OrderHistoryComponent implements OnInit {
  orders: Order[] = [];
  isLoading = true;
  error: string | null = null;

  private userId = localStorage.getItem('userId'); 

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchOrders();
  }

  fetchOrders(): void {
    this.isLoading = true;
    this.error = null;
    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    this.http.get<Order[]>(`${environment.endpoints.userBaseEndpoint}/${this.userId}/orders`, { headers: headers as { [header: string]: string | string[] } })
      .pipe(
        catchError(err => {
          console.error('Error fetching orders:', err);
          this.error = 'Failed to load order history. Please try again later.';
          this.isLoading = false;
          return of([]);
        })
      )
      .subscribe(data => {
        this.orders = data;
        this.isLoading = false;
      });
  }

  formatAddress(address: Address): string {
    return `${address.line1}${address.line2 ? ', ' + address.line2 : ''}, ${address.city}, ${address.state} ${address.postalCode}, ${address.country}`;
  }
}