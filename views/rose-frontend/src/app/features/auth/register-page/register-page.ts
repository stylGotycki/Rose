import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Auth } from '../../../core/auth/auth';

@Component({
  selector: 'app-register-page',
  imports: [FormsModule, RouterLink],
  templateUrl: './register-page.html',
  styleUrl: './register-page.css',
})
export class RegisterPage {
  private readonly auth = inject(Auth);

  firstName = '';
  lastName = '';
  email = '';
  password = '';

  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  submit(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);

    if (!this.firstName || !this.lastName || !this.email || !this.password) {
      this.errorMessage.set('Uzupełnij wszystkie pola.');
      return;
    }

    if (this.password.length < 8) {
      this.errorMessage.set('Hasło musi mieć co najmniej 8 znaków.');
      return;
    }

    this.loading.set(true);

    this.auth
      .register({
        firstName: this.firstName,
        lastName: this.lastName,
        email: this.email,
        password: this.password,
      })
      .subscribe({
        next: () => {
          this.loading.set(false);
          this.successMessage.set(
            'Konto zostało utworzone. Sprawdź wiadomość email i aktywuj konto.',
          );

          this.firstName = '';
          this.lastName = '';
          this.email = '';
          this.password = '';
        },
        error: (error) => {
          console.error('Register error:', error);
          this.loading.set(false);
          this.errorMessage.set('Nie udało się utworzyć konta.');
        },
      });
  }
}