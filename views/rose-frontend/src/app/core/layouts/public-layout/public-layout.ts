import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { PublicNavbar } from '../public-navbar/public-navbar';
import { PublicFooter } from '../public-footer/public-footer';

@Component({
  selector: 'app-public-layout',
  imports: [RouterOutlet, PublicNavbar, PublicFooter],
  templateUrl: './public-layout.html',
  styleUrl: './public-layout.css',
})
export class PublicLayout {}