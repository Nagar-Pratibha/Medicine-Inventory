import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-contact-us',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './contact-us.component.html',
  styleUrls: ['./contact-us.component.css']
})
export class ContactUsComponent {
  contactForm = {
    fullName: '',
    email: '',
    subject: '',
    message: ''
  };

  onSendMessage() {
    console.log('Message sent:', this.contactForm);
    // In a real app, you would send this to a server
    alert('Thank you for your message! We will get back to you shortly.');
    this.contactForm = { fullName: '', email: '', subject: '', message: '' }; // Reset form
  }
}