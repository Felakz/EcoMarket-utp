import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit {
  stats = {
    totalProducts: 0,
    totalUsers: 0,
    totalOrders: 0,
    totalRevenue: 0
  };

  recentActivities: any[] = [];

  constructor() {}

  ngOnInit(): void {
    this.loadDashboardStats();
    this.loadRecentActivities();
  }

  private loadDashboardStats(): void {
    // TODO: Load real stats from services
    this.stats = {
      totalProducts: 24,
      totalUsers: 156,
      totalOrders: 89,
      totalRevenue: 15420.50
    };
  }

  private loadRecentActivities(): void {
    // TODO: Load real activities from services
    this.recentActivities = [
      {
        id: 1,
        type: 'product_added',
        message: 'Nuevo producto agregado: "Bolsa Ecológica Premium"',
        timestamp: new Date(),
        user: 'Admin'
      },
      {
        id: 2,
        type: 'order_completed',
        message: 'Pedido #1234 completado',
        timestamp: new Date(Date.now() - 3600000),
        user: 'Sistema'
      },
      {
        id: 3,
        type: 'user_registered',
        message: 'Nuevo usuario registrado: maria@example.com',
        timestamp: new Date(Date.now() - 7200000),
        user: 'Sistema'
      }
    ];
  }

  getActivityIcon(type: string): string {
    switch (type) {
      case 'product_added': return '🛍️';
      case 'order_completed': return '✅';
      case 'user_registered': return '👤';
      default: return '📝';
    }
  }
}