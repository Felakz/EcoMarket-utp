import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CartService, CartItem } from '../../services/cart.service';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent implements OnInit {
  cartItems: CartItem[] = [];
  total: number = 0;
  checkoutForm: FormGroup;

  constructor(
    private cartService: CartService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    this.checkoutForm = this.formBuilder.group({
      // Información personal
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^[\+]?[0-9\-\(\)\s]+$/)]],

      // Dirección de envío
      address: ['', [Validators.required, Validators.minLength(10)]],
      city: ['', [Validators.required]],
      state: ['', [Validators.required]],
      zipCode: ['', [Validators.required, Validators.pattern(/^[0-9]{5}(-[0-9]{4})?$/)]],
      country: ['Colombia', [Validators.required]],

      // Método de pago
      paymentMethod: ['card', [Validators.required]],
      cardNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{16}$/)]],
      expiryDate: ['', [Validators.required, Validators.pattern(/^(0[1-9]|1[0-2])\/[0-9]{2}$/)]],
      cvv: ['', [Validators.required, Validators.pattern(/^[0-9]{3,4}$/)]],
      cardName: ['', [Validators.required, Validators.minLength(2)]],

      // Términos y condiciones
      acceptTerms: [false, [Validators.requiredTrue]]
    });
  }

  ngOnInit(): void {
    this.cartService.getCartItems().subscribe((items: any) => {
      this.cartItems = items;
      if (items.length === 0) {
        this.router.navigate(['/products']);
      }
    });

    this.cartService.getCartTotal().subscribe((total: any) => {
      this.total = total;
    });

    // Mostrar/ocultar campos de tarjeta según el método de pago
    this.checkoutForm.get('paymentMethod')?.valueChanges.subscribe((value: any) => {
      this.updatePaymentValidators(value);
    });
  }

  private updatePaymentValidators(paymentMethod: string): void {
    const cardFields = ['cardNumber', 'expiryDate', 'cvv', 'cardName'];

    if (paymentMethod === 'card') {
      cardFields.forEach(field => {
        this.checkoutForm.get(field)?.setValidators([Validators.required]);
      });
    } else {
      cardFields.forEach(field => {
        this.checkoutForm.get(field)?.clearValidators();
      });
    }

    cardFields.forEach(field => {
      this.checkoutForm.get(field)?.updateValueAndValidity();
    });
  }

  onSubmit(): void {
    if (this.checkoutForm.valid && this.cartItems.length > 0) {
      // Aquí iría la lógica para procesar el pago
      console.log('Procesando orden:', {
        customer: this.checkoutForm.value,
        items: this.cartItems,
        total: this.total
      });

      // Simular procesamiento
      alert('¡Orden procesada exitosamente! Gracias por tu compra.');

      // Limpiar carrito y redirigir
      this.cartService.clearCart();
      this.router.navigate(['/']);
    } else {
      // Marcar todos los campos como touched para mostrar errores
      Object.keys(this.checkoutForm.controls).forEach(key => {
        this.checkoutForm.get(key)?.markAsTouched();
      });
    }
  }

  getItemTotal(item: CartItem): number {
    return item.product.price * item.quantity;
  }

  goBack(): void {
    this.router.navigate(['/cart']);
  }
}