import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AppNavbar } from '../app-navbar/app-navbar';

@Component({
  selector: 'app-app-layout',
  imports: [RouterOutlet, AppNavbar],
  templateUrl: './app-layout.html',
  styleUrl: './app-layout.css',
})
export class AppLayout {}