import { Component, OnDestroy } from '@angular/core';
import { ApiService, CollaborationResponse } from './api.service';
import { Subscription, interval } from 'rxjs';
import { switchMap, takeWhile } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnDestroy {
  fileName = '';
  selectedFile: File | null = null;
  errorMsg = '';

  progress = 0;
  phase: 'idle' | 'upload' | 'processing' | 'done' = 'idle';
  private timerSub?: Subscription;
  private apiSub?: Subscription;

  summary: CollaborationResponse['summary'] | null = null;
  details: CollaborationResponse['details'] = [];

  constructor(private api: ApiService) {}

  onFileSelected(event: Event): void {
    this.reset();
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.selectedFile = input.files[0];
      this.fileName = this.selectedFile.name;
    }
  }

  onProcess(): void {
    if (!this.selectedFile) {
      this.errorMsg = 'Please select a CSV file first.';
      return;
    }
    this.errorMsg = '';
    this.summary = null;
    this.details = [];
    this.startUploadSimulation();
  }

  private startUploadSimulation(): void {
    this.phase = 'upload';
    this.progress = 0;
    this.timerSub?.unsubscribe();
    this.timerSub = interval(500)
        .pipe(takeWhile(() => this.progress < 100))
        .subscribe(() => {
          this.progress += 20;
          if (this.progress >= 100) {
            this.timerSub?.unsubscribe();
            this.startProcessingSimulation();
          }
        });
  }

  private startProcessingSimulation(): void {
    this.phase = 'processing';
    this.progress = 0;
    this.timerSub?.unsubscribe();
    this.timerSub = interval(500)
        .pipe(takeWhile(() => this.progress < 100))
        .subscribe(() => {
          this.progress += 20;
          if (this.progress >= 100) {
            this.timerSub?.unsubscribe();
            this.callApi();
          }
        });
  }

  private callApi(): void {
    if (!this.selectedFile) return;
    const fd = new FormData();
    fd.append('file', this.selectedFile);

    this.apiSub = this.api.processCsvWithProgress(fd)
        .subscribe({
          next: () => {},
          error: err => {
            this.errorMsg = err.error || 'Error processing CSV.';
          }
        });

    this.apiSub = this.api.processCsvWithProgress(fd)
        .subscribe(event => {
          if (event.type === 4) {
            this.summary = event.body?.summary || null;
            this.details = event.body?.details || [];
            this.phase = 'done';
          }
        });
  }

  private reset(): void {
    this.errorMsg = '';
    this.phase = 'idle';
    this.progress = 0;
    this.timerSub?.unsubscribe();
    this.apiSub?.unsubscribe();
  }

  ngOnDestroy(): void {
    this.timerSub?.unsubscribe();
    this.apiSub?.unsubscribe();
  }
}
