import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-auth-layout',
  standalone: true,
  imports: [RouterOutlet, CommonModule],
  templateUrl: './auth-layout.component.html',
  styleUrls: ['./auth-layout.component.scss']
})
export class AuthLayoutComponent implements OnInit, OnDestroy {
  images: string[] = [
    '/agricultora.jpg',
    '/highfive.jpg',
    '/señoritas.jpg'
  ];
  
  currentImageIndex: number = 0;
  private intervalId: any;

  ngOnInit(): void {
    this.preloadImages(); // Precargar al inicio
    this.startImageRotation();
  }

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  // NUEVO MÉTODO: Carga las imágenes en la caché del navegador silenciosamente
  preloadImages(): void {
    this.images.forEach((src) => {
      const img = new Image();
      img.src = src;
    });
  }

  startImageRotation(): void {
    this.intervalId = setInterval(() => {
      this.currentImageIndex = (this.currentImageIndex + 1) % this.images.length;
    }, 4000);
  }
}