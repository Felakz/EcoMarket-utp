import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Product } from '../../models/product.model';
import { ProductsService } from '../../services/products.service';
import { CartService } from '../../services/cart.service';
import { ProductReviewsComponent } from '../product-reviews/product-reviews.component';
import { AddReviewComponent } from '../add-review/add-review.component';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, ProductReviewsComponent, AddReviewComponent],
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss']
})
export class ProductDetailComponent implements OnInit {
  product: Product | undefined;
  quantity: number = 1;
  loading: boolean = true;

  private readonly placeholders: { [key: string]: string } = {
    'Muebles Ecológicos': 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&h=600&fit=crop',
    'Accesorios Sostenibles': 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=800&h=600&fit=crop',
    'Hogar Eco-Friendly': 'https://images.unsplash.com/photo-1484101403633-562f891dc89a?w=800&h=600&fit=crop',
    'default': 'https://images.unsplash.com/photo-1532453288672-3a27e9be9efd?w=800&h=600&fit=crop'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productsService: ProductsService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.productsService.getProductById(id).subscribe({
        next: (product: any) => {
          this.product = product;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
          // Handle error - product not found
          this.router.navigate(['/products']);
        }
      });
    }
  }

  addToCart(): void {
    if (this.product) {
      this.cartService.addToCart(this.product, this.quantity);
      // Optional: Show success message or navigate to cart
    }
  }

  increaseQuantity(): void {
    this.quantity++;
  }

  decreaseQuantity(): void {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  goBack(): void {
    this.router.navigate(['/products']);
  }

  getImageUrl(): string {
    if (!this.product) {
      return this.placeholders['default'];
    }
    // Si no hay imageFilename, usar placeholder directamente
    if (!this.product.imageFilename) {
      return this.placeholders[this.product.categoryName] || this.placeholders['default'];
    }
    // Si hay imageFilename, usar la URL del backend
    return this.product.imageUrl;
  }

  onImageError(event: Event): void {
    // Si la imagen falla, usar un placeholder genérico
    const imgElement = event.target as HTMLImageElement;
    imgElement.src = 'https://images.unsplash.com/photo-1532453288672-3a27e9be9efd?w=800&h=600&fit=crop';
  }

  onReviewAdded(): void {
    // The ProductReviewsComponent will automatically refresh when a new review is added
    // This method can be used for additional logic if needed
  }
}