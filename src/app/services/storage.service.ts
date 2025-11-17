import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  private isBrowser: boolean;
  private memory: Record<string, string> = {};

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  getItem(key: string): string | null {
    if (this.isBrowser && typeof window !== 'undefined' && window.localStorage) {
      return window.localStorage.getItem(key);
    }
    return this.memory.hasOwnProperty(key) ? this.memory[key] : null;
  }

  setItem(key: string, value: string): void {
    if (this.isBrowser && typeof window !== 'undefined' && window.localStorage) {
      window.localStorage.setItem(key, value);
      return;
    }
    this.memory[key] = value;
  }

  removeItem(key: string): void {
    if (this.isBrowser && typeof window !== 'undefined' && window.localStorage) {
      window.localStorage.removeItem(key);
      return;
    }
    if (this.memory.hasOwnProperty(key)) {
      delete this.memory[key];
    }
  }
}
