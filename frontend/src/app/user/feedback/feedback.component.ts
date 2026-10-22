import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// Define the structure of a feedback item
interface Feedback {
  name: string;
  date: string;
  rating: number;
  message: string;
}

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.css']
})
export class FeedbackComponent {
  // Initial mock feedback data
  feedbacks: Feedback[] = [
    {
      name: 'Sarah J.',
      date: '2 days ago',
      rating: 4,
      message: 'The new interface is much cleaner and easier to navigate. Finding the medicines I need is a breeze now. Great work!'
    },
    {
      name: 'Mike R.',
      date: '5 days ago',
      rating: 5,
      message: 'Excellent service and a very intuitive platform. The checkout process was seamless. Highly recommended.'
    },
    {
      name: 'Devika Agarwal',
      date: '2 days ago',
      rating: 5,
      message: 'I have been through phases where i need medicines urgently and cure cart delivered them on time. thankyou!'
    },
    {
      name: 'Sai Satwik',
      date: '10 days ago',
      rating: 4.5,
      message: 'Tremendous services provided. well done curecart'
    },
    {
      name: 'Ravi kumar',
      date: '15 days ago',
      rating: 4,
      message: 'Excellent work Cure Cart Team.'
    },
    {
      name: 'Sowmitha Ravikumar',
      date: '3 days ago',
      rating: 5,
      message: 'great work Team Cure Cart.'
    }
  ];

  isModalOpen = false;
  newFeedback = {
    rating: 0,
    message: ''
  };

  // --- Modal Controls ---
  openModal() {
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
    this.resetForm();
  }

  // --- Form Handling ---
  setRating(rating: number) {
    this.newFeedback.rating = rating;
  }

  resetForm() {
    this.newFeedback.rating = 0;
    this.newFeedback.message = '';
  }

  onSubmitFeedback() {
    if (this.newFeedback.rating === 0 || !this.newFeedback.message) {
      alert('Please provide a rating and a message.');
      return;
    }

    // Create a new feedback object
    const feedbackToAdd: Feedback = {
      name: 'You', // As the current user
      date: 'Just now',
      rating: this.newFeedback.rating,
      message: this.newFeedback.message
    };

    // Add it to the top of the array
    this.feedbacks.unshift(feedbackToAdd);

    // Close the modal
    this.closeModal();
  }
}