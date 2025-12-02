import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Product } from '../../models/product.model';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.scss']
})
export class ProductCardComponent {
  @Input() product!: Product;

  private readonly placeholders: { [key: string]: string } = {
    'Muebles Ecológicos': 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400&h=300&fit=crop',
    'Accesorios Sostenibles': 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=400&h=300&fit=crop',
    'Hogar Eco-Friendly': 'https://images.unsplash.com/photo-1484101403633-562f891dc89a?w=400&h=300&fit=crop',
    'default': 'https://images.unsplash.com/photo-1532453288672-3a27e9be9efd?w=400&h=300&fit=crop'
  };

  constructor(private cartService: CartService) {}

  getImageUrl(): string {
    // Si no hay imageFilename, usar placeholder directamente
    if (!this.product.imageFilename) {
      return this.placeholders[this.product.categoryName] || this.placeholders['default'];
    }
    // Si hay imageFilename, usar la URL del backend
    return this.product.imageUrl;
  }

  addToCart(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    this.cartService.addToCart(this.product);
  }

  onImageError(event: Event): void {
    // Si la imagen falla, usar un placeholder genérico
    const imgElement = event.target as HTMLImageElement;
    imgElement.src = 'https://images.unsplash.com/photo-1532453288672-3a27e9be9efd?w=400&h=300&fit=crop';
  }
}
