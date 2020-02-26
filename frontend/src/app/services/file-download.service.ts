import { Injectable } from '@angular/core';

@Injectable()
export class FileDownloadService {

  download(file: Blob, fileName: string) {
    const url = window.URL.createObjectURL(file);

    const a = document.createElement('a');
    document.body.appendChild(a);
    a.setAttribute('style', 'display: none');
    a.setAttribute('target', 'blank');
    a.href = url;
    a.download = fileName;
    a.click();
    window.URL.revokeObjectURL(url);
    a.remove();
  }
}
