import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { environment } from '../../environments/environment.development';
 
export interface Address {
  id: string;
  country: string;
  postalCode: string;
  addressLine1: string;
  addressLine2?: string; // Optional field
  state: string;
  isDefault: boolean;
}
 
@Injectable({
  providedIn: 'root'
})
export class AddressService {
  private addresses: Address[] = [];

  showToast = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';

  private addressesSubject = new BehaviorSubject<Address[]>(this.addresses);
  addresses$ = this.addressesSubject.asObservable();

  constructor(private http: HttpClient) { }
 

  addAddress(address: Omit<Address, 'id' | 'isDefault'>) {
    const newAddress: Address = {
      ...address,
      id: Date.now().toString(),
      isDefault: this.addresses.length === 0
    };
    this.addresses.push(newAddress);
    this.addressesSubject.next(this.addresses);
  }
 
 onSetDefault(addressId: string) {
    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};
    const setDefaultUrl = `${environment.endpoints.userBaseEndpoint}/${localStorage.getItem('userId')}/addresses/${addressId}/default`;
    // Return the observable so the component can subscribe to it
    return this.http.put(setDefaultUrl, {}, { headers: headers as { [header: string]: string | string[] } });
  }
 
  // onDeleteAddress(addressId: string) {
  //   if (confirm('Are you sure you want to delete this address?')) {
  //     const authToken = localStorage.getItem('authToken');
  //     const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};
  //     const deleteUrl = `${this.addressApiUrl}/${addressId}`;

  //     this.http.delete(deleteUrl, { headers: headers as { [header: string]: string | string[] } }).subscribe(() => {
  //       this.showSuccessToast('Address deleted.');
  //     });
  //   }
  // }

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