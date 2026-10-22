import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { environment } from '../../../environments/environment.development';

// Matches OrderItemResponseDTO
interface OrderItem {
  orderItemId: number;
  medicineName: string;
  quantity: number;
  priceAtPurchase: number | null;
}

// Matches AddressResponseDTO
interface Address {
  line1: string;
  line2: string | null;
  city: string;
  state: string;
  postalCode: string;
}

// Matches OrderResponseDTO
type OrderStatus = 'PENDING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
interface Order {
  orderId: number;
  userId: number;
  address: Address;
  totalAmount: number;
  status: OrderStatus;
  createdAt: string;
  items: OrderItem[];
}

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-orders.component.html',
  styleUrls: ['./admin-orders.component.css']
})
export class AdminOrdersComponent implements OnInit {
  allOrders: Order[] = [];
  pendingActionOrders: Order[] = [];
  processedOrders: Order[] = [];

  isModalOpen = false;
  selectedOrder: Order | null = null;

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.fetchOrders();
  }

  fetchOrders() {
    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    this.http.get<Order[]>(`${environment.endpoints.fetchAllOrdersAdmin}`, { headers: headers as { [header: string]: string | string[] } }).pipe(
      catchError(err => {
        console.error('Error fetching orders:', err);
        return of([]);
      })
    ).subscribe(orders => {
      console.log(orders);
      this.allOrders = orders.map(o => ({ ...o})); 
      this.filterOrders();
    });
  }

  filterOrders() {
    this.pendingActionOrders = this.allOrders.filter(o => o.status === 'PENDING');
    this.processedOrders = this.allOrders.filter(o => o.status !== 'PENDING');
  }

  shipOrder(orderToShip: Order) {
    this.updateOrderStatus(orderToShip.orderId, 'SHIPPED');
  }

  cancelOrder(orderToCancel: Order) {
    this.updateOrderStatus(orderToCancel.orderId, 'CANCELLED');
  }

  updateOrderStatus(orderId: number, newStatus: OrderStatus) {
    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};
    const updateUrl = `${environment.endpoints.userBaseEndpoint}/${localStorage.getItem('userId')}/orders/${orderId}/status`;

    // backend expects 'status' as a request parameter, not in the body.
    const params = new HttpParams().set('status', newStatus);

    // send an empty body `{}` because the data is in the URL parameter.
    this.http.put(updateUrl, {}, { headers: headers as { [header: string]: string | string[] }, params }).pipe(
      catchError(err => {
        console.error(`Failed to update order ${orderId} to ${newStatus}`, err);
        alert(`Error updating order status. Please try again.`);
        return of(null);
      })
    ).subscribe(response => {
      if (response) {
        console.log(`Order ${orderId} successfully updated to ${newStatus}`);
        // Find the order in the local array and update its status
        const order = this.allOrders.find(o => o.orderId === orderId);
        if (order) {
          order.status = newStatus;
          this.filterOrders();
        }
      }
    });
  }

  viewOrder(order: Order) {
    this.selectedOrder = order;
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
    this.selectedOrder = null;
  }

  get pendingOrdersCount(): number {
    return this.allOrders.filter(o => o.status === 'PENDING').length;
  }

  get shippedOrdersCount(): number {
    return this.allOrders.filter(o => o.status === 'SHIPPED' || o.status === 'DELIVERED').length;
  }

  get totalRevenue(): number {
    return this.allOrders.reduce((sum, order) => sum + order.totalAmount, 0);
  }
}