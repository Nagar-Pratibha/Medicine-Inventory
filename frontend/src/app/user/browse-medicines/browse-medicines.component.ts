import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService, ServerCart } from '../../services/cart.service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { environment } from '../../../environments/environment.development';

interface Medicine {
  id: string; name: string; category: string; stock: number; price: number; description: string; quantityInCart: number; cartItemId: number | null;
}

@Component({
  selector: 'app-browse-medicines',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './browse-medicines.component.html',
  styleUrls: ['./browse-medicines.component.css']
})
export class BrowseMedicinesComponent implements OnInit {
  allMedicines: Medicine[] = [];
  filteredMedicines: Medicine[] = [];
  categories: string[] = [];

  searchTerm = '';
  selectedCategory = 'All';

  constructor(private http: HttpClient, private cartService: CartService) { }

  ngOnInit() {
    // fetchMedicines() calls syncWithCart() once the catalogue has loaded,
    // so there is no need to call it a second time here.
    this.fetchMedicines();
  }

  /** Lets Angular reuse the existing card DOM instead of rebuilding the grid. */
  trackByMedicineId(_index: number, medicine: Medicine): string {
    return medicine.id;
  }

  fetchMedicines() {
    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    this.http.get<any[]>(`${environment.endpoints.medicineBaseEndpoint}`, { headers: headers as { [header: string]: string | string[] } }).subscribe({
      next: (response) => {
        this.allMedicines = response.map(med => ({
          id: med.id.toString(),
          name: med.name,
          category: med.compositionText || 'Uncategorized',
          stock: med.stock,
          price: med.price,
          description: med.description || `${med.pack_size} by ${med.manufacture_name}`,
          quantityInCart: 0,
          cartItemId: null
        }));
        this.filteredMedicines = this.allMedicines;
        this.categories = ['All', ...new Set(this.allMedicines.map(m => m.category))];
        this.syncWithCart();
      },
      error: (error) => {
        console.error('Failed to fetch medicines:', error);
        alert('Failed to load medicines. Please try again later.');
      }
    });
  }

  syncWithCart() {
    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    this.http.get<ServerCart>(`${environment.endpoints.userBaseEndpoint}/${localStorage.getItem('userId')}/cart`, { headers: headers as { [header: string]: string | string[] } }).subscribe(cart => {
      this.applyCart(cart);
    });
  }

  /**
   * Projects the cart returned by the backend onto the medicine list that is
   * already on screen. Every /cart endpoint returns the full CartResponseDTO,
   * so this keeps the UI in step without re-fetching the catalogue.
   */
  private applyCart(cart: ServerCart | null | undefined) {
    if (!cart) {
      return;
    }

    const items = cart.items ?? [];
    this.allMedicines.forEach(med => {
      const itemInCart = items.find(item => item.medicineId === +med.id);
      med.quantityInCart = itemInCart ? itemInCart.quantity : 0;
      med.cartItemId = itemInCart ? itemInCart.cartItemId : null;
    });

    this.filterMedicines();
    // Keep shared cart state (and the sidebar badge) in sync.
    this.cartService.setCartFromServer(items);
  }

  filterMedicines() {
    let tempMeds = this.allMedicines;

    if (this.selectedCategory !== 'All') {
      tempMeds = tempMeds.filter(med => med.category === this.selectedCategory);
    }

    if (this.searchTerm) {
      tempMeds = tempMeds.filter(med =>
        med.name.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }

    this.filteredMedicines = tempMeds;
  }

  increaseQuantity(medicine: Medicine) {
    const userId = localStorage.getItem('userId');
    const authToken = localStorage.getItem('authToken');
    if (!userId) {
      alert('You must be logged in to add items to the cart.');
      return;
    }

    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};
    const payload = {
      medicineId: +medicine.id,
      quantity: 1 // one at a time
    };

    const request = this.http.post<ServerCart>(`${environment.endpoints.userBaseEndpoint}/${userId}/cart/items`, payload, { headers: headers as { [header: string]: string | string[] } });

    request.pipe(
      catchError(err => {
        console.error('Error increasing quantity:', err);
        alert(`Failed to update cart for ${medicine.name}.`);
        return of(null);
      })
    ).subscribe(response => {
      // The response is the full updated cart, so apply it directly instead of
      // re-fetching the medicine list (which rebuilt the whole grid).
      this.applyCart(response);
    });
  }

  decreaseQuantity(medicine: Medicine) {
    console.log(medicine.quantityInCart);
    if (medicine.quantityInCart <= 0) return;

    const userId = localStorage.getItem('userId');
    const authToken = localStorage.getItem('authToken');
    if (!userId || !medicine.cartItemId) {
      alert('An error occurred. Please refresh and try again.');
      return;
    }

    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    const params = new HttpParams().set('quantity', medicine.quantityInCart - 1);

    const request = this.http.put<ServerCart>(`${environment.endpoints.userBaseEndpoint}/${userId}/cart/items/${medicine.cartItemId}`, {}, { headers: headers as { [header: string]: string | string[] }, params });
    request.pipe(
      catchError(err => {
        console.log('Error decreasing quantity:', err);
        alert(`Failed to update cart for ${medicine.name}.`);
        return of(null);
      })
    ).subscribe(response => {
      // Apply the updated cart from the response; on failure applyCart() is a
      // no-op so the displayed quantity no longer drifts from the server.
      this.applyCart(response);
    });
  }
}