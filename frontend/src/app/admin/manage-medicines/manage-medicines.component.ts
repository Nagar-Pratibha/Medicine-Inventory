import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { environment } from '../../../environments/environment.development';

interface Medicine {
  id: string; 
  name: string; 
  category: string;
  stock: number; 
  price: number; 
  description: string;
  manufacture_name: string;
  pack_size: string;
}


interface MedicineUpdatePayload {
  name: string;
  description: string;
  price: number;
  stock: number;
  manufacture_name: string;
  pack_size: string;
  compositionText: string;
}

interface MedicineResponse {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  manufacture_name: string;
  pack_size: string;
  compositionText: string;
}

@Component({
  selector: 'app-manage-medicines',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manage-medicines.component.html',
  styleUrls: ['./manage-medicines.component.css']
})
export class ManageMedicinesComponent implements OnInit {
  allMedicines: Medicine[] = [];
  
  isAddDialogOpen = false;
  isEditDialogOpen = false;
  editingMedicine: Medicine | null = null;
  
  addFormData: any = {};
  editFormData: any = {};


  constructor(private http: HttpClient) { }

  ngOnInit(): void {
      this.fetchMedicines();
  }

  fetchMedicines() {
    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    this.http.get<any[]>(environment.endpoints.medicineBaseEndpoint, { headers: headers as { [header: string]: string | string[] } }).subscribe({
      next: (response) => {
        
        this.allMedicines = response.map(med => ({
          id: med.id.toString(),
          name: med.name,
          category: med.compositionText || 'Uncategorized', 
          stock: med.stock,
          price: med.price,
          description: med.description || '',
          manufacture_name: med.manufacture_name || '',
          pack_size: med.pack_size || ''
        }));
      },
      error: (error) => {
        console.error('Failed to fetch medicines:', error);
        alert('Failed to load medicines. Please try again later.');
      }
    });
  }

  openAddDialog() {
    this.editingMedicine = null;
    this.addFormData = { 
      name: '', 
      description: '',
      price: 0, 
      stock: 0, 
      manufacture_name: '',
      pack_size: '',
      category: '' 
    };
    this.isAddDialogOpen = true;
  }

  openEditDialog(medicine: Medicine) {
    this.editingMedicine = medicine;
    this.editFormData = {
      name: medicine.name,
      description: medicine.description,
      price: medicine.price,
      stock: medicine.stock,
      category: medicine.category
    };
    this.isEditDialogOpen = true;
  }

  closeDialogs() {
    this.isAddDialogOpen = false;
    this.isEditDialogOpen = false;
  }

  onAddSubmit() {
    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    const payload = {
      name: this.addFormData.name,
      description: this.addFormData.description,
      price: this.addFormData.price,
      stock: this.addFormData.stock,
      manufacture_name: this.addFormData.manufacture_name,
      pack_size: this.addFormData.pack_size,
      compositionText: this.addFormData.category
    };

    this.http.post<MedicineResponse>(environment.endpoints.medicineBaseEndpoint, payload, { headers: headers as { [header:string]: string | string[] } }).pipe(
      catchError(err => {
        console.error('Failed to add medicine', err);
        alert('An error occurred while adding the new medicine.');
        return of(null);
      })
    ).subscribe(response => {
      if (response) {
        console.log('Medicine added successfully', response);
        this.fetchMedicines(); 
      }
    });
    this.closeDialogs();
  }

  onEditSubmit() {
    if (!this.editingMedicine) return;

    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    const payload = {
      name: this.editFormData.name,
      description: this.editFormData.description,
      price: this.editFormData.price,
      stock: this.editFormData.stock,
      compositionText: this.editFormData.category
    };

    const updateUrl = `${environment.endpoints.medicineBaseEndpoint}/${this.editingMedicine.id}`;
    this.http.put<MedicineResponse>(updateUrl, payload, { headers: headers as { [header: string]: string | string[] } }).pipe(
      catchError(err => {
        console.error('Medicine update failed', err);
        alert('An error occurred while updating the medicine.');
        return of(null);
      })
    ).subscribe(response => {
      if (response) {
        console.log('Medicine updated successfully', response);
        this.fetchMedicines(); 
      }
    });
    this.closeDialogs();
  }

  deleteMed(medicine: Medicine) {

    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};
    this.http.delete(`${environment.endpoints.medicineBaseEndpoint}/${medicine.id}`, {headers: headers as { [header: string]: string | string[] }}).subscribe({
        next: () => {
          console.log('Medicine deleted successfully');
          this.fetchMedicines(); 
        },
        error: (error) => {
          console.error('Failed to delete medicine:', error);
          alert('Failed to delete medicine. Please try again later.');
        }
      });
  }
}