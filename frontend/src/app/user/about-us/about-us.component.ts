import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-about-us',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './about-us.component.html',
  styleUrls: ['./about-us.component.css']
})
export class AboutUsComponent {
  teamMembers = [
    {
      name: 'Divyansh Khare',
      role: 'Chief Executive Officer',
      imageUrl: 'https://via.placeholder.com/150'
    },
    {
      name: 'Harsh Sonkar',
      role: 'Chief Technology Officer',
      imageUrl: 'https://via.placeholder.com/150'
    },
    {
      name: 'Dr Pratibha Nagar',
      role: 'Senior Oncology Specialist',
      imageUrl: 'https://via.placeholder.com/150'
    },
    {
      name: 'Dr Arnav Raina',
      role: 'PHD Research Scientist',
      imageUrl: 'https://via.placeholder.com/150'
    },
    {
      name: 'Dr Muskan Bahari',
      role: 'Senior Neurology Specialist',
      imageUrl: 'https://via.placeholder.com/150'
    }
  ];
}