import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AddressService } from '../../services/address.service';
import { environment } from '../../../environments/environment.development';

// Matches the backend Address DTO
interface BackendAddress {
  addressId: number;
  line1: string;
  line2: string | null;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  default: boolean;
}

// Matches the backend UserDTO
interface UserUpdatePayload {
  name: string | null;
  email: string | null;
  password?: string; // Optional
}

// Matches the backend UserResponseDTO
interface UserResponse {
  id: number;
  name: string;
  email: string;
  role: 'ADMIN' | 'USER';
}

export interface Address {
  id: string;
  country: string;
  postalCode: string;
  addressLine1: string;
  addressLine2?: string;
  state: string;
  city: string;
  isDefault: boolean;
}

@Component({
  selector: 'app-my-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.css'],
})
export class MyProfileComponent implements OnInit {
  profile = {
    name: '',
    email: '',
    password: '',
    role: '',
  };
  isSaving = false;

  addresses: Address[] = [];
  isAddAddressModalOpen = false;

  newAddress = {
    country: 'India',
    postalCode: '',
    addressLine1: '',
    addressLine2: '',
    state: '',
    city: ''
  };

  showToast = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';

  constructor(private http: HttpClient, private router: Router, private addressService: AddressService) { }

  ngOnInit(): void {
    this.profile.name = localStorage.getItem('userName') || '';
    this.profile.email = localStorage.getItem('userEmail') || '';
    this.profile.role = localStorage.getItem('userRole')?.toUpperCase() || 'USER';
    this.fetchAddresses();
  }

  fetchAddresses() {
    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    this.http.get<BackendAddress[]>(`${environment.endpoints.userBaseEndpoint}/${localStorage.getItem('userId')}/addresses`, { headers: headers as { [header: string]: string | string[] } }).pipe(
      catchError(err => {
        console.error('Error fetching addresses:', err);
        this.showErrorToast('Failed to load your saved addresses.');
        return of([]);
      })
    ).subscribe(backendAddresses => {
      this.addresses = backendAddresses.map(addr => ({
        id: addr.addressId.toString(),
        country: addr.country,
        postalCode: addr.postalCode,
        addressLine1: addr.line1,
        addressLine2: addr.line2 || '',
        state: addr.state,
        city: addr.city,
        isDefault: addr.default
      }));
    });
  }

  onProfileSubmit() {
    const userId = localStorage.getItem('userId');
    if (!userId) {
      alert('Error: User not found. Please log in again.');
      return;
    }

    this.isSaving = true;

    const payload: UserUpdatePayload = {
      name: this.profile.name,
      email: this.profile.email
    };

    // Only include the password if the user has entered one
    if (this.profile.password) {
      payload.password = this.profile.password;
    }

    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    this.http.put<UserResponse>(`${environment.endpoints.userBaseEndpoint}/${userId}`, payload, { headers: headers as { [header: string]: string | string[] } }).pipe(
      catchError(err => {
        console.error('Profile update failed', err);
        alert('An error occurred while updating your profile. Please try again.');
        this.isSaving = false;
        return of(null);
      })
    ).subscribe(response => {
      if (response) {
        console.log('Profile updated successfully', response);

        
        localStorage.setItem('userName', response.name);
        localStorage.setItem('userEmail', response.email);

        
        this.profile.name = response.name;
        this.profile.email = response.email;
        this.profile.password = ''; 

        this.showSuccessToast('Profile updated successfully!');
      }
      this.isSaving = false;
    });
  }

  onSetDefault(id: string) { 
    this.addressService.onSetDefault(id).subscribe(() => {
      this.showSuccessToast('Default address updated.');

      this.fetchAddresses();
    }); 
  }


  openAddAddressModal() { this.isAddAddressModalOpen = true; }
  closeAddAddressModal() { this.isAddAddressModalOpen = false; }

  onAddAddress() {
    if (!this.newAddress.addressLine1 || !this.newAddress.postalCode || !this.newAddress.city || !this.newAddress.state) {
      this.showErrorToast('Please fill out all required fields.');
      return;
    }

    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    const payload = {
      line1: this.newAddress.addressLine1,
      line2: this.newAddress.addressLine2,
      city: this.newAddress.city,
      state: this.newAddress.state,
      postalCode: this.newAddress.postalCode,
      country: this.newAddress.country,
      isDefault: this.addresses.length === 0,
      userId: localStorage.getItem('userId')
    };

    this.http.post<BackendAddress>(`${environment.endpoints.userBaseEndpoint}/${localStorage.getItem('userId')}/addresses`, payload, { headers: headers as { [header: string]: string | string[] } }).pipe(
      catchError(err => {
        console.error('Error adding address:', err);
        this.showErrorToast('Failed to save your new address.');
        return of(null);
      })
    ).subscribe(savedAddress => {
      if (savedAddress) {
        this.showSuccessToast('Address saved successfully!');
        this.fetchAddresses();
        this.closeAddAddressModal();
        this.newAddress = { country: 'India', postalCode: '', addressLine1: '', addressLine2: '', state: '', city: '' };
      }
    });
  }

  private showSuccessToast(message: string) {
    this.toastMessage = message;
    this.toastType = 'success';
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }

  private showErrorToast(message: string) {
    this.toastMessage = message;
    this.toastType = 'error';
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }
}