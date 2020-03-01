import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpHeaders,
  HttpResponse,
  HttpErrorResponse
} from '@angular/common/http';

import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { createFileWrapper } from 'src/app/common/utils';
import { FileDownloadService, NotificationService } from 'src/app/common/services';

@Injectable()
export class GlobalHTTPInterceptor implements HttpInterceptor {

  constructor(
    private fileDownloadService: FileDownloadService,
    private notificationService: NotificationService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const modifiedReq = req.clone({
      headers: new HttpHeaders({
        'Content-type': 'application/json',
      })
    });

    return next.handle(modifiedReq)
      .pipe(map((ev: HttpEvent<any>) => {
        if (ev instanceof HttpResponse) {
          return this.handleHttpResponse(ev);
        }
      })
      ).pipe(catchError((err: HttpErrorResponse) => {
        return this.handleHttpErrorResponse(err);
      }));
  }

  private handleHttpResponse(ev: HttpResponse<any>) {
    const contentType = ev.headers.get('content-type');

    if (contentType === 'application/octet-stream') {
      this.fileDownloadService.download(createFileWrapper(ev));
    }

    return ev;
  }

  private handleHttpErrorResponse(err: HttpErrorResponse) {
    this.notificationService.error(err.error.message);
    return throwError(err);
  }
}
