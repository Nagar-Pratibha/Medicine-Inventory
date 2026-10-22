import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

export type UserRole = 'admin' | 'user' | null;

export interface User {
  id: number;
  name: string;
  email: string;
  role: 'ADMIN' | 'USER';
}


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserRole: UserRole = null;
  private apiUrl = 'http://localhost:8099/api/auth/login';
  private registerUrl = 'http://localhost:8099/api/users/register';


  constructor(private http: HttpClient, private router: Router) {

    const storedRole = localStorage.getItem('userRole');
    if (storedRole) {
      this.currentUserRole = storedRole as UserRole;
    }
  }

  login(email: string, password: string): void {
    const loginData = { email, password };
    console.log(loginData);

    this.http.post<{ token: string, user: User }>(`${this.apiUrl}`, loginData).subscribe({ // http.post instantly returns Observable object, so instead of app being freezed
      next: (response) => {                                                                // the app works (async). next is executed if successfull otherwise error
        console.log('Login successful', response);


        localStorage.setItem('authToken', response.token);
        localStorage.setItem('userId', response.user.id.toString());
        localStorage.setItem('userName', response.user.name);
        localStorage.setItem('userEmail', response.user.email);

        if (response.user && response.user.role) {
          this.currentUserRole = response.user.role.toLowerCase() as UserRole;
          console.log('User role:', this.currentUserRole);
          localStorage.setItem('userRole', this.currentUserRole as string);
          if (this.currentUserRole === 'admin') {
            this.router.navigate(['/admin']);
          } else if (this.currentUserRole === 'user') {
            this.router.navigate(['/browse-medicines']);
          } else {
            alert('Invalid role. Please contact support.');
          }
        } else {
          alert('Role is missing in the response. Please contact support.');
        }
      },
      error: (error) => {
        console.error('Login failed', error);
        alert('Login failed. Please check your credentials and try again.');
      }
    });
  }


  logout(): void {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
    localStorage.removeItem('userEmail');
    this.currentUserRole = null;
    this.router.navigate(['/signin']);
  }

  signup(name: string, email: string, password: string) {
    const signupData = { name, email, password };
    this.http.post(`${this.registerUrl}`, signupData).subscribe({
      next: (response) => {
        console.log('Signup successful', response);
        alert('Signup successful! Please sign in.');
        this.router.navigate(['/signin']);
      },
      error: (error) => {
        console.error('Signup failed', error);
        alert('Signup failed. Please try again.');
      }
    });
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('authToken');
  }

  hasRole(role: UserRole): boolean {
    return this.currentUserRole === role;
  }
}