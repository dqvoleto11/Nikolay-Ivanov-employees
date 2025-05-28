import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpEventType } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PairResult { emp1: number; emp2: number; days: number; }
export interface DetailedResult { emp1: number; emp2: number; projectId: number; days: number; }
export interface CollaborationResponse { summary: PairResult; details: DetailedResult[]; }

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = 'http://localhost:8080/api';
  constructor(private http: HttpClient) {}

  processCsvWithProgress(data: FormData): Observable<HttpEvent<CollaborationResponse>> {
    return this.http.post<CollaborationResponse>(`${this.baseUrl}/process`, data, {
      reportProgress: true,
      observe: 'events'
    });
  }
}
