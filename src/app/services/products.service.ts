import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, of, catchError, map } from 'rxjs';
import { Product } from '../models/product.model';

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
  private readonly API_URL = 'http://localhost:8080/ecomarket/api/products';
  private products: Product[] = [];
  private productsSubject = new BehaviorSubject<Product[]>(this.products);
  public products$ = this.productsSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadProducts();
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
      ? `http://localhost:8080/ecomarket/api/images/${response.imageFilename}` 
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
      catchError(() => {
        // Fallback to mock data
        const mockProduct = this.getMockProducts().find(p => p.id === id);
        return of(mockProduct);
      }),
      map((response: ProductResponse | undefined) => response ? this.mapToProduct(response) : undefined)
    );
  }

  getProductsByCategory(category: string): Observable<Product[]> {
    return this.http.get<{ content: ProductResponse[] }>(this.API_URL, {
      params: new HttpParams()
        .set('category', category)
        .set('size', '100')
    }).pipe(
      catchError(() => of({ content: this.getMockProducts().filter(p => p.category === category) })),
      map((response: { content: ProductResponse[] }) => response.content.map(this.mapToProduct))
    );
  }

  searchProducts(query: string): Observable<Product[]> {
    return this.http.get<{ content: ProductResponse[] }>(this.API_URL, {
      params: new HttpParams()
        .set('search', query)
        .set('size', '100')
    }).pipe(
      catchError(() => {
        const filtered = this.getMockProducts().filter(p =>
          p.name.toLowerCase().includes(query.toLowerCase()) ||
          p.description.toLowerCase().includes(query.toLowerCase())
        );
        return of({ content: filtered });
      }),
      map((response: { content: ProductResponse[] }) => response.content.map(this.mapToProduct))
    );
  }

  createProduct(product: Omit<Product, 'id' | 'createdAt' | 'updatedAt'>): Observable<Product> {
    const request: ProductRequest = {
      name: product.name,
      description: product.description,
      price: product.price,
      category: product.category,
      ecoRating: product.ecoRating,
      sustainabilityScore: product.sustainabilityScore,
      carbonFootprint: product.carbonFootprint,
      tags: product.tags,
      inStock: product.inStock,
      imageUrl: product.imageUrl
    };

    return this.http.post<ProductResponse>(this.API_URL, request).pipe(
      catchError(() => {
        // Fallback: simulate creation with mock data
        const newProduct = { ...product, id: Date.now(), createdAt: new Date(), updatedAt: new Date() };
        this.products.push(newProduct);
        this.productsSubject.next([...this.products]);
        return of(this.mapToProductResponse(newProduct));
      }),
      map((response: ProductResponse) => {
        const newProduct = this.mapToProduct(response);
        this.products.push(newProduct);
        this.productsSubject.next([...this.products]);
        return newProduct;
      })
    );
  }

  updateProduct(id: number, updates: Partial<Product>): Observable<Product | null> {
    const request: Partial<ProductRequest> = {
      name: updates.name,
      description: updates.description,
      price: updates.price,
      category: updates.category,
      ecoRating: updates.ecoRating,
      sustainabilityScore: updates.sustainabilityScore,
      carbonFootprint: updates.carbonFootprint,
      tags: updates.tags,
      inStock: updates.inStock,
      imageUrl: updates.imageUrl
    };

    return this.http.put<ProductResponse>(`${this.API_URL}/${id}`, request).pipe(
      catchError(() => {
        // Fallback: simulate update with mock data
        const index = this.products.findIndex(p => p.id === id);
        if (index !== -1) {
          this.products[index] = { ...this.products[index], ...updates, updatedAt: new Date() };
          this.productsSubject.next([...this.products]);
          return of(this.mapToProductResponse(this.products[index]));
        }
        return of(null);
      }),
      map((response: ProductResponse | null) => {
        if (response) {
          const updatedProduct = this.mapToProduct(response);
          const index = this.products.findIndex(p => p.id === id);
          if (index !== -1) {
            this.products[index] = updatedProduct;
            this.productsSubject.next([...this.products]);
          }
          return updatedProduct;
        }
        return null;
      })
    );
  }

  deleteProduct(id: number): Observable<boolean> {
    return this.http.delete(`${this.API_URL}/${id}`).pipe(
      catchError(() => {
        // Fallback: simulate deletion with mock data
        const index = this.products.findIndex(p => p.id === id);
        if (index !== -1) {
          this.products.splice(index, 1);
          this.productsSubject.next([...this.products]);
          return of(true);
        }
        return of(false);
      }),
      map(() => {
        const index = this.products.findIndex(p => p.id === id);
        if (index !== -1) {
          this.products.splice(index, 1);
          this.productsSubject.next([...this.products]);
        }
        return true;
      })
    );
  }

  getFeatured(): Product[] {
    return this.products.slice(0, 3);
  }

  getCategories(): string[] {
    return [...new Set(this.products.map(p => p.category))];
  }

  private mapToProduct(response: ProductResponse): Product {
    const imageUrl = response.imageUrl || (response.imageFilename 
      ? `http://localhost:8080/ecomarket/api/images/${response.imageFilename}` 
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

  private mapToProductResponse(product: Product): ProductResponse {
    return {
      id: product.id,
      name: product.name,
      description: product.description,
      price: product.price,
      imageUrl: product.imageUrl,
      category: product.category,
      ecoRating: product.ecoRating,
      sustainabilityScore: product.sustainabilityScore,
      carbonFootprint: product.carbonFootprint,
      tags: product.tags,
      inStock: product.inStock,
      createdAt: product.createdAt.toISOString(),
      updatedAt: product.updatedAt.toISOString()
    };
  }

  private getMockProducts(): ProductResponse[] {
    return [
      {
        id: 1,
        name: 'Bolsa de Tela Reciclada',
        description: 'Bolsa resistente hecha de materiales reciclados, perfecta para compras ecológicas.',
        price: 25.99,
        imageUrl: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&h=300&fit=crop',
        category: 'Accesorios',
        ecoRating: 5,
        sustainabilityScore: 95,
        carbonFootprint: 2.5,
        tags: ['reciclado', 'bolsa', 'ecológico'],
        inStock: true,
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z'
      },
      {
        id: 2,
        name: 'Maceta de Plástico Reciclado',
        description: 'Maceta biodegradable para tus plantas, hecha 100% de plástico reciclado.',
        price: 15.50,
        imageUrl: 'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400&h=300&fit=crop',
        category: 'Jardinería',
        ecoRating: 4,
        sustainabilityScore: 88,
        carbonFootprint: 1.8,
        tags: ['plástico reciclado', 'maceta', 'biodegradable'],
        inStock: true,
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z'
      },
      {
        id: 3,
        name: 'Cartera de Cuero Vegano',
        description: 'Cartera elegante hecha de cuero vegano, sostenible y duradera.',
        price: 45.00,
        imageUrl: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&h=300&fit=crop',
        category: 'Accesorios',
        ecoRating: 5,
        sustainabilityScore: 92,
        carbonFootprint: 3.2,
        tags: ['cuero vegano', 'cartera', 'sostenible'],
        inStock: true,
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z'
      },
      {
        id: 4,
        name: 'Botella de Agua Reutilizable',
        description: 'Botella de acero inoxidable, mantiene el agua fría por horas.',
        price: 18.99,
        imageUrl: 'https://images.unsplash.com/photo-1523362628745-0c100150b504?w=400&h=300&fit=crop',
        category: 'Lifestyle',
        ecoRating: 4,
        sustainabilityScore: 90,
        carbonFootprint: 1.5,
        tags: ['acero inoxidable', 'botella', 'reutilizable'],
        inStock: true,
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z'
      },
      {
        id: 5,
        name: 'Juguete de Madera Reciclada',
        description: 'Juguete educativo para niños, hecho de madera reciclada.',
        price: 12.99,
        imageUrl: 'https://images.unsplash.com/photo-1558877385-1199c1af40a0?w=400&h=300&fit=crop',
        category: 'Niños',
        ecoRating: 4,
        sustainabilityScore: 85,
        carbonFootprint: 2.0,
        tags: ['madera reciclada', 'juguete', 'educativo'],
        inStock: true,
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z'
      },
      {
        id: 6,
        name: 'Camiseta Orgánica',
        description: 'Camiseta cómoda hecha de algodón orgánico certificado.',
        price: 22.50,
        imageUrl: 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400&h=300&fit=crop',
        category: 'Ropa',
        ecoRating: 5,
        sustainabilityScore: 93,
        carbonFootprint: 2.8,
        tags: ['algodón orgánico', 'camiseta', 'certificado'],
        inStock: true,
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z'
      }
    ];
  }
}
