import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  constructor(private matSnackBar: MatSnackBar) {}

  notify(notification: string) {
    this.matSnackBar.open(notification, '', {
      duration: 5000,
      verticalPosition: 'top',
    });
  }

  apiError(error: { error: { message: string } }) {
    this.notify(error.error.message);
  }
}
