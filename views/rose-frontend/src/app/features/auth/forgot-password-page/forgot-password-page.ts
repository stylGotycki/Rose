import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Auth } from '../../../core/auth/auth';

@Component({
  selector: 'app-forgot-password-page',
  imports: [FormsModule, RouterLink],
  templateUrl: './forgot-password-page.html',
  styleUrl: './forgot-password-page.css',
})
export class ForgotPasswordPage {
  private readonly auth = inject(Auth);

  email = '';

  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  submit(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);

    if (!this.email) {
      this.errorMessage.set('Podaj adres email.');
      return;
    }

    this.loading.set(true);

    this.auth.forgotPassword({ email: this.email }).subscribe({
      next: () => {
        this.loading.set(false);
        this.successMessage.set('Wysłano wiadomość z linkiem resetującym hasło.');
      },
      error: (error) => {
        console.error('Forgot password error:', error);
        this.loading.set(false);
        this.errorMessage.set('Nie udało się wysłać wiadomości.');
      },
    });
  }
}