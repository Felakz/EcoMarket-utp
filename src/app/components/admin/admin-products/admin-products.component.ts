import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Product } from '../../../models/product.model';
import { ProductsService } from '../../../services/products.service';

@Component({
  selector: 'app-admin-products',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './admin-products.component.html',
  styleUrls: ['./admin-products.component.scss']
})
export class AdminProductsComponent implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  loading: boolean = true;
  searchTerm: string = '';
  selectedCategory: string = '';
  categories: string[] = [];

  constructor(private productsService: ProductsService) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadProducts();
  }

  private loadCategories(): void {
    this.categories = ['Todas', ...this.productsService.getCategories()];
  }

  private loadProducts(): void {
    this.loading = true;
    this.productsService.getProducts().subscribe((products: any) => {
      this.products = products;
      this.filteredProducts = products;
      this.loading = false;
    });
  }

  onSearch(): void {
    this.filterProducts();
  }

  onCategoryChange(): void {
    this.filterProducts();
  }

  private filterProducts(): void {
    let filtered = this.products;

    // Filter by search term
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(product =>
        product.name.toLowerCase().includes(term) ||
        product.description.toLowerCase().includes(term) ||
        product.category.toLowerCase().includes(term)
      );
    }

    // Filter by category
    if (this.selectedCategory && this.selectedCategory !== 'Todas') {
      filtered = filtered.filter(product =>
        product.category === this.selectedCategory
      );
    }

    this.filteredProducts = filtered;
  }

  deleteProduct(productId: number): void {
    if (confirm('¿Estás seguro de que quieres eliminar este producto? Esta acción no se puede deshacer.')) {
      this.productsService.deleteProduct(productId).subscribe({
        next: (success: boolean) => {
          if (success) {
            this.products = this.products.filter(p => p.id !== productId);
            this.filterProducts();
          } else {
            alert('Error al eliminar el producto. Inténtalo de nuevo.');
          }
        },
        error: (error: any) => {
          console.error('Error deleting product:', error);
          alert('Error al eliminar el producto. Inténtalo de nuevo.');
        }
      });
    }
  }

  toggleProductStatus(product: Product): void {
    const newStatus = !product.inStock;
    const action = newStatus ? 'poner en stock' : 'agotar';

    if (confirm(`¿Estás seguro de que quieres ${action} este producto?`)) {
      this.productsService.updateProduct(product.id, { inStock: newStatus }).subscribe({
        next: (updatedProduct: Product | null) => {
          if (updatedProduct) {
            // Update local array
            const index = this.products.findIndex(p => p.id === product.id);
            if (index !== -1) {
              this.products[index] = updatedProduct;
              this.filterProducts();
            }
          } else {
            alert('Error al actualizar el estado del producto. Inténtalo de nuevo.');
          }
        },
        error: (error: any) => {
          console.error('Error updating product status:', error);
          alert('Error al actualizar el estado del producto. Inténtalo de nuevo.');
        }
      });
    }
  }

  getStockStatus(product: Product): string {
    return product.inStock ? 'En Stock' : 'Agotado';
  }

  getStockStatusClass(product: Product): string {
    return product.inStock ? 'text-green-600 bg-green-100' : 'text-red-600 bg-red-100';
  }
}