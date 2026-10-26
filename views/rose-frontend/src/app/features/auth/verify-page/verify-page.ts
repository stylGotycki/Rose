import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Auth } from '../../../core/auth/auth';

@Component({
  selector: 'app-verify-page',
  imports: [RouterLink],
  templateUrl: './verify-page.html',
  styleUrl: './verify-page.css',
})
export class VerifyPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly auth = inject(Auth);

  readonly loading = signal(true);
  readonly success = signal(false);
  readonly message = signal('Trwa weryfikacja konta...');

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.loading.set(false);
      this.success.set(false);
      this.message.set('Brak tokenu weryfikacyjnego w adresie URL.');
      return;
    }

    this.auth.verify(token).subscribe({
      next: () => {
        this.loading.set(false);
        this.success.set(true);
        this.message.set('Konto zostało aktywowane. Możesz się zalogować.');
      },
      error: (error) => {
        console.error('Verify error:', error);
        this.loading.set(false);
        this.success.set(false);
        this.message.set('Nie udało się aktywować konta. Token może być niepoprawny lub wygasły.');
      },
    });
  }
}