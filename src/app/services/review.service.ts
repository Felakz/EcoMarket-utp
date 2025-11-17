import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { Review } from '../models/review.model';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private reviews: Review[] = [
    {
      id: 1,
      productId: 1,
      userId: 1,
      userName: 'María González',
      rating: 5,
      title: 'Excelente calidad y entrega rápida',
      comment: 'Excelente producto, muy buena calidad y llegó antes de lo esperado. ¡Recomendado!',
      createdAt: new Date('2024-11-01'),
      updatedAt: new Date('2024-11-01'),
      helpful: 12,
      verified: true
    },
    {
      id: 2,
      productId: 1,
      userId: 2,
      userName: 'Carlos Rodríguez',
      rating: 4,
      title: 'Buena bolsa, pequeño detalle en el color',
      comment: 'Buena bolsa, resistente y útil. El único detalle es que esperaba un color diferente.',
      createdAt: new Date('2024-11-05'),
      updatedAt: new Date('2024-11-05'),
      helpful: 8,
      verified: true
    },
    {
      id: 3,
      productId: 2,
      userId: 3,
      userName: 'Ana López',
      rating: 5,
      title: 'Perfecta para mis plantas',
      comment: 'Perfecta para mis plantas. Es biodegradable y se ve muy bien en el jardín.',
      createdAt: new Date('2024-11-03'),
      updatedAt: new Date('2024-11-03'),
      helpful: 15,
      verified: true
    },
    {
      id: 4,
      productId: 3,
      userId: 4,
      userName: 'Pedro Martínez',
      rating: 4,
      title: 'Elegante y cómoda',
      comment: 'Cartera muy elegante y cómoda. El cuero vegano se siente igual que el cuero natural.',
      createdAt: new Date('2024-11-07'),
      updatedAt: new Date('2024-11-07'),
      helpful: 6,
      verified: false
    },
    {
      id: 5,
      productId: 4,
      userId: 5,
      userName: 'Laura Sánchez',
      rating: 5,
      title: 'Mantiene el agua fría por horas',
      comment: 'La botella mantiene el agua fría por horas. Perfecta para el día a día.',
      createdAt: new Date('2024-11-02'),
      updatedAt: new Date('2024-11-02'),
      helpful: 20,
      verified: true
    }
  ];

  private reviewsSubject = new BehaviorSubject<Review[]>(this.reviews);
  public reviews$ = this.reviewsSubject.asObservable();

  constructor() { }

  getReviewsByProductId(productId: number): Observable<Review[]> {
    const productReviews = this.reviews.filter(review => review.productId === productId);
    return of(productReviews);
  }

  getAverageRating(productId: number): Observable<number> {
    const productReviews = this.reviews.filter(review => review.productId === productId);
    if (productReviews.length === 0) return of(0);

    const average = productReviews.reduce((sum, review) => sum + review.rating, 0) / productReviews.length;
    return of(Math.round(average * 10) / 10);
  }

  getReviewCount(productId: number): Observable<number> {
    const productReviews = this.reviews.filter(review => review.productId === productId);
    return of(productReviews.length);
  }

  addReview(review: Omit<Review, 'id' | 'createdAt' | 'updatedAt'>): Observable<Review> {
    const newReview: Review = {
      ...review,
      id: Math.max(...this.reviews.map(r => r.id)) + 1,
      createdAt: new Date(),
      updatedAt: new Date()
    };

    this.reviews.push(newReview);
    this.reviewsSubject.next([...this.reviews]);
    return of(newReview);
  }

  markHelpful(reviewId: number): void {
    const review = this.reviews.find(r => r.id === reviewId);
    if (review) {
      review.helpful++;
      this.reviewsSubject.next([...this.reviews]);
    }
  }

  getAllReviews(): Observable<Review[]> {
    return of(this.reviews);
  }
}