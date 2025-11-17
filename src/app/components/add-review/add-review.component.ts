import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReviewService } from '../../services/review.service';

@Component({
  selector: 'app-add-review',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './add-review.component.html',
  styleUrls: ['./add-review.component.scss']
})
export class AddReviewComponent {
  @Input() productId!: number;
  @Output() reviewAdded = new EventEmitter<void>();

  reviewForm: FormGroup;
  submitting: boolean = false;
  showForm: boolean = false;
  selectedRating: number = 0;
  hoverRating: number = 0;

  constructor(
    private fb: FormBuilder,
    private reviewService: ReviewService
  ) {
    this.reviewForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(100)]],
      comment: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]],
      rating: [0, [Validators.required, Validators.min(1), Validators.max(5)]]
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
    if (!this.showForm) {
      this.resetForm();
    }
  }

  setRating(rating: number): void {
    this.selectedRating = rating;
    this.reviewForm.patchValue({ rating });
  }

  setHoverRating(rating: number): void {
    this.hoverRating = rating;
  }

  clearHoverRating(): void {
    this.hoverRating = 0;
  }

  getStarArray(): number[] {
    return Array.from({ length: 5 }, (_, i) => i + 1);
  }

  getStarClass(star: number): string {
    const rating = this.hoverRating || this.selectedRating;
    return star <= rating ? 'text-yellow-400' : 'text-gray-300';
  }

  onSubmit(): void {
    if (this.reviewForm.valid && this.selectedRating > 0) {
      this.submitting = true;

      const reviewData = {
        productId: this.productId,
        userId: 1, // TODO: Get from authentication service
        userName: 'Usuario Anónimo', // TODO: Get from authentication service
        rating: this.selectedRating,
        title: this.reviewForm.value.title,
        comment: this.reviewForm.value.comment,
        helpful: 0,
        verified: false
      };

      this.reviewService.addReview(reviewData).subscribe({
        next: () => {
          this.submitting = false;
          this.resetForm();
          this.showForm = false;
          this.reviewAdded.emit();
        },
        error: (error: any) => {
          console.error('Error adding review:', error);
          this.submitting = false;
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  private resetForm(): void {
    this.reviewForm.reset();
    this.selectedRating = 0;
    this.hoverRating = 0;
  }

  private markFormGroupTouched(): void {
    Object.keys(this.reviewForm.controls).forEach(key => {
      const control = this.reviewForm.get(key);
      control?.markAsTouched();
    });
  }

  get title() { return this.reviewForm.get('title'); }
  get comment() { return this.reviewForm.get('comment'); }
  get rating() { return this.reviewForm.get('rating'); }
}