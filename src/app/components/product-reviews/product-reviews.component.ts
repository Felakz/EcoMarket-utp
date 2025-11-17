import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Review } from '../../models/review.model';
import { ReviewService } from '../../services/review.service';

@Component({
  selector: 'app-product-reviews',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-reviews.component.html',
  styleUrls: ['./product-reviews.component.scss']
})
export class ProductReviewsComponent implements OnInit {
  @Input() productId!: number;
  reviews: Review[] = [];
  averageRating: number = 0;
  reviewCount: number = 0;
  loading: boolean = true;

  constructor(private reviewService: ReviewService) {}

  ngOnInit(): void {
    this.loadReviews();
  }

  private loadReviews(): void {
    this.loading = true;

    // Load reviews
    this.reviewService.getReviewsByProductId(this.productId).subscribe((reviews: any) => {
      this.reviews = reviews;
      this.loading = false;
    });

    // Load average rating
    this.reviewService.getAverageRating(this.productId).subscribe((rating: any) => {
      this.averageRating = rating;
    });

    // Load review count
    this.reviewService.getReviewCount(this.productId).subscribe((count: any) => {
      this.reviewCount = count;
    });
  }

  markHelpful(reviewId: number): void {
    this.reviewService.markHelpful(reviewId);
  }

  getStarArray(rating: number): number[] {
    return Array.from({ length: 5 }, (_, i) => i + 1);
  }

  getRatingPercentage(): number {
    return (this.averageRating / 5) * 100;
  }
}