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
  isOrganic: boolean;
  certifications: string | null;
  originCountry: string | null;
  createdAt: Date;
  updatedAt: Date;
}