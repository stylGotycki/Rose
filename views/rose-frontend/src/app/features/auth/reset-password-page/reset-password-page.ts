import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Auth } from '../../../core/auth/auth';

@Component({
  selector: 'app-reset-password-page',
  imports: [FormsModule, RouterLink],
  templateUrl: './reset-password-page.html',
  styleUrl: './reset-password-page.css',
})
export class ResetPasswordPage {
  private readonly auth = inject(Auth);
  private readonly route = inject(ActivatedRoute);

  newPassword = '';

  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  submit(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.errorMessage.set('Brak tokenu resetowania hasła.');
      return;
    }

    if (this.newPassword.length < 8) {
      this.errorMessage.set('Nowe hasło musi mieć co najmniej 8 znaków.');
      return;
    }

    this.loading.set(true);

    this.auth
      .resetPassword({
        token,
        newPassword: this.newPassword,
      })
      .subscribe({
        next: () => {
          this.loading.set(false);
          this.successMessage.set('Hasło zostało zmienione. Możesz się zalogować.');
          this.newPassword = '';
        },
        error: (error) => {
          console.error('Reset password error:', error);
          this.loading.set(false);
          this.errorMessage.set('Nie udało się zmienić hasła.');
        },
      });
  }
}