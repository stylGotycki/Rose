import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../../../core/auth/auth';

@Component({
  selector: 'app-login-page',
  imports: [FormsModule, RouterLink],
  templateUrl: './login-page.html',
  styleUrl: './login-page.css',
})
export class LoginPage {
  private readonly auth = inject(Auth);
  private readonly router = inject(Router);

  email = '';
  password = '';

  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  submit(): void {
    this.errorMessage.set(null);

    if (!this.email || !this.password) {
      this.errorMessage.set('Podaj email i hasło.');
      return;
    }

    this.loading.set(true);

    this.auth
      .login({
        email: this.email,
        password: this.password,
      })
      .subscribe({
        next: () => {
          this.loading.set(false);
          this.router.navigate(['/app/restaurants']);
        },
        error: (error) => {
          console.error('Login error:', error);
          this.loading.set(false);

          if (error.status === 401 || error.status === 403) {
            this.errorMessage.set(
              'Nie udało się zalogować. Sprawdź dane lub upewnij się, że konto jest aktywowane.',
            );
            return;
          }

          this.errorMessage.set('Wystąpił błąd podczas logowania.');
        },
      });
  }
}