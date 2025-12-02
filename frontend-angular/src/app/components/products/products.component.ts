import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductCardComponent } from '../product-card/product-card.component';
import { Product } from '../../models/product.model';
import { ProductsService } from '../../services/products.service';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, FormsModule, ProductCardComponent],
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  categories: string[] = [];
  selectedCategory = '';
  searchQuery = '';
  sortBy = 'name';
  loading = true;

  // Nuevos filtros
  minPrice: number = 0;
  maxPrice: number = 100;
  showOnlyAvailable: boolean = false;
  minEcoRating: number = 0;

  constructor(private productsService: ProductsService) {}

  ngOnInit(): void {
    this.loadProducts();
    this.categories = this.productsService.getCategories();
  }

  loadProducts(): void {
    this.loading = true;
    this.productsService.getProducts().subscribe((products: any) => {
      this.products = products;
      this.filteredProducts = products;
      this.loading = false;
    });
  }

  onSearch(): void {
    this.applyFilters();
  }

  onCategoryChange(): void {
    this.applyFilters();
  }

  onSortChange(): void {
    this.applyFilters();
  }

  private applyFilters(): void {
    let filtered = this.products;

    // Filter by category
    if (this.selectedCategory) {
      filtered = filtered.filter(p => p.categoryName === this.selectedCategory);
    }

    // Filter by search query
    if (this.searchQuery) {
      filtered = filtered.filter(p =>
        p.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        p.description.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    }

    // Filter by price range
    filtered = filtered.filter(p => p.price >= this.minPrice && p.price <= this.maxPrice);

    // Filter by availability
    if (this.showOnlyAvailable) {
      filtered = filtered.filter(p => p.stock > 0);
    }

    // Sort
    filtered.sort((a, b) => {
      switch (this.sortBy) {
        case 'price-low':
          return a.price - b.price;
        case 'price-high':
          return b.price - a.price;
        case 'name':
        default:
          return a.name.localeCompare(b.name);
      }
    });

    this.filteredProducts = filtered;
  }

  clearFilters(): void {
    this.selectedCategory = '';
    this.searchQuery = '';
    this.sortBy = 'name';
    this.minPrice = 0;
    this.maxPrice = 100;
    this.showOnlyAvailable = false;
    this.filteredProducts = this.products;
  }
}