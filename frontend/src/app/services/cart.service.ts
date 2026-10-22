import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface CartItem {
  id: string; name: string; quantity: number; price: number;
}

// Shape returned by the backend (CartResponseDTO / CartItemResponseDTO)
export interface ServerCartItem {
  cartItemId: number;
  medicineId: number;
  medicineName: string;
  price: number;
  quantity: number;
  subtotal: number;
}

export interface ServerCart {
  cartId: number;
  userId: number;
  items: ServerCartItem[];
  totalAmount: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartItemsSource = new BehaviorSubject<CartItem[]>([]);
  currentCart = this.cartItemsSource.asObservable();

  addToCart(item: CartItem) {
    const currentItems = this.cartItemsSource.getValue();
    const existingItem = currentItems.find(cartItem => cartItem.id === item.id);
    if (existingItem) {
      existingItem.quantity += 1;
    } else {
      currentItems.push({ ...item, quantity: 1 });
    }
    this.cartItemsSource.next(currentItems);
  }

  /**
   * Replaces the local cart with the authoritative cart returned by the backend.
   * Call this with the CartResponseDTO that every /cart endpoint already returns,
   * so subscribers (e.g. the sidebar badge) update without an extra round trip.
   */
  setCartFromServer(items: ServerCartItem[] | null | undefined) {
    const mapped: CartItem[] = (items ?? []).map(item => ({
      id: item.medicineId.toString(),
      name: item.medicineName,
      quantity: item.quantity,
      price: item.price
    }));
    this.cartItemsSource.next(mapped);
  }

  getCartItems() {
    return this.cartItemsSource.getValue();
  }

  clearCart() {
    this.cartItemsSource.next([]);
  }
}