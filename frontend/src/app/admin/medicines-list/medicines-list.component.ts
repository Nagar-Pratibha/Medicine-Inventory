import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment.development';

interface Medicine {
  id: string; 
  name: string; 
  category: string; 
  stock: number; 
  price: number; 
  description: string;
}

@Component({
  selector: 'app-medicines-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './medicines-list.component.html',
  styleUrls: ['./medicines-list.component.css']
})
export class MedicinesListComponent implements OnInit {
  allMedicines: Medicine[] = [];

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
      this.fetchAllMedicines();
  }
  
  fetchAllMedicines() {
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
          description: med.description || `${med.pack_size} by ${med.manufacture_name}`
        }));
      },
      error: (error) => {
        console.error('Failed to fetch medicines:', error);
        alert('Failed to load medicines. Please try again later.');
      }
    });
  }

  isLowStock(stock: number): boolean {
    return stock < 50; // Threshold
  }

  isEmptyStock(stock: number): boolean {
    return stock === 0;
  }
}