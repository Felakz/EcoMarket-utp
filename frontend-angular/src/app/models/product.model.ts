export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  categoryId: number;
  categoryName: string;
  imageFilename: string | null;
  imageUrl: string | null;
  isOrganic: boolean | null;
  certifications: string | null;
  originCountry: string | null;
  createdAt: string | Date;
  updatedAt: string | Date;
  // Additional fields used across components
  category?: string; // alias for categoryName
  inStock?: boolean; // alias for stock > 0
  ecoRating?: number;
  sustainabilityScore?: number;
  carbonFootprint?: number;
  tags?: string[];
}