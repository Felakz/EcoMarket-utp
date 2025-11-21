import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, of, catchError, map } from 'rxjs';
import { Product } from '../models/product.model';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../environments/environment';

interface ProductResponse {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  categoryId: number;
  categoryName: string;
  imageFilename: string | null;
  imageUrl: string | null;
  isOrganic: boolean;
  certifications: string | null;
  originCountry: string | null;
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProductsService {
  private readonly API_URL = `${environment.apiUrl.replace(/\/+$/, '')}/products`;
  private products: Product[] = [];
  private productsSubject = new BehaviorSubject<Product[]>(this.products);
  public products$ = this.productsSubject.asObservable();

  constructor(private http: HttpClient, @Inject(PLATFORM_ID) private platformId: Object) {
    // Only load products automatically when running in the browser.
    // This prevents SSR/build-time attempts to contact localhost (which fail on Vercel).
    if (isPlatformBrowser(this.platformId)) {
      this.loadProducts();
    }
  }

  private loadProducts(): void {
    this.http.get<{ content: ProductResponse[] }>(this.API_URL, {
      params: new HttpParams().set('size', '100')
    }).pipe(
      catchError((error) => {
        console.error('Error loading products:', error);
        return of({ content: [] });
      }),
      map((response: { content: ProductResponse[] }) => response.content.map(this.mapToProduct.bind(this)))
    ).subscribe((products: Product[]) => {
      this.products = products;
      this.productsSubject.next([...this.products]);
    });
  }

  getProducts(): Observable<Product[]> {
    return this.http.get<{ content: ProductResponse[] }>(this.API_URL, {
      params: new HttpParams().set('size', '100')
    }).pipe(
      catchError((error) => {
        console.error('Error getting products:', error);
        return of({ content: [] });
      }),
      map((response: { content: ProductResponse[] }) => response.content.map(this.mapToProduct.bind(this)))
    );
  }

  getProductById(id: number): Observable<Product | undefined> {
    return this.http.get<ProductResponse>(`${this.API_URL}/${id}`).pipe(
      catchError((error) => {
        console.error('Error getting product by id:', error);
        return of(undefined);
      }),
      map((response: ProductResponse | undefined) => response ? this.mapToProduct(response) : undefined)
    );
  }

  getProductsByCategory(categoryName: string): Observable<Product[]> {
    return this.http.get<{ content: ProductResponse[] }>(this.API_URL, {
      params: new HttpParams()
        .set('category', categoryName)
        .set('size', '100')
    }).pipe(
      catchError((error) => {
        console.error('Error getting products by category:', error);
        return of({ content: [] });
      }),
      map((response: { content: ProductResponse[] }) => response.content.map(this.mapToProduct.bind(this)))
    );
  }

  searchProducts(query: string): Observable<Product[]> {
    return this.http.get<{ content: ProductResponse[] }>(this.API_URL, {
      params: new HttpParams()
        .set('search', query)
        .set('size', '100')
    }).pipe(
      catchError((error) => {
        console.error('Error searching products:', error);
        return of({ content: [] });
      }),
      map((response: { content: ProductResponse[] }) => response.content.map(this.mapToProduct.bind(this)))
    );
  }

  getFeatured(): Product[] {
    return this.products.slice(0, 3);
  }

  getCategories(): string[] {
    return [...new Set(this.products.map(p => p.categoryName))];
  }

  private mapToProduct(response: ProductResponse): Product {
    const imageUrl = response.imageUrl || (response.imageFilename
      ? `${environment.apiUrl.replace(/\/+$/, '')}/images/${response.imageFilename}`
      : 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=400&h=300&fit=crop');
    
    return {
      id: response.id,
      name: response.name,
      description: response.description,
      price: response.price,
      stock: response.stock,
      categoryId: response.categoryId,
      categoryName: response.categoryName,
      imageFilename: response.imageFilename,
      imageUrl: imageUrl,
      isOrganic: response.isOrganic,
      certifications: response.certifications,
      originCountry: response.originCountry,
      createdAt: new Date(response.createdAt),
      updatedAt: new Date(response.updatedAt)
    };
  }

  // --- Minimal create/update/delete implementations used by admin components ---
  createProduct(product: Omit<Product, 'id' | 'createdAt' | 'updatedAt'>): Observable<Product> {
    const newProduct: Product = {
      ...product as Product,
      id: Date.now(),
      createdAt: new Date(),
      updatedAt: new Date(),
      // ensure aliases
      category: (product as any).category || product.categoryName,
      inStock: (product as any).inStock ?? (product.stock > 0),
    };
    this.products.push(newProduct);
    this.productsSubject.next([...this.products]);
    return of(newProduct);
  }

  updateProduct(id: number, updates: Partial<Product>): Observable<Product | null> {
    const index = this.products.findIndex(p => p.id === id);
    if (index === -1) return of(null);
    const updated = { ...this.products[index], ...updates, updatedAt: new Date() } as Product;
    this.products[index] = updated;
    this.productsSubject.next([...this.products]);
    return of(updated);
  }

  deleteProduct(id: number): Observable<boolean> {
    const index = this.products.findIndex(p => p.id === id);
    if (index === -1) return of(false);
    this.products.splice(index, 1);
    this.productsSubject.next([...this.products]);
    return of(true);
  }

  private mapToProductResponse(product: Product) {
    // Minimal mapping for fallback/mock usage
    return {
      id: product.id,
      name: product.name,
      description: product.description,
      price: product.price,
      stock: product.stock,
      categoryId: product.categoryId ?? 0,
      categoryName: product.categoryName ?? product.category ?? '',
      imageFilename: product.imageFilename ?? null,
      imageUrl: product.imageUrl ?? null,
      isOrganic: product.isOrganic ?? false,
      certifications: product.certifications ?? null,
      originCountry: product.originCountry ?? null,
      createdAt: product.createdAt.toISOString(),
      updatedAt: product.updatedAt.toISOString(),
    } as ProductResponse;
  }
}

