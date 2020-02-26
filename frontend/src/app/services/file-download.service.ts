import { Injectable } from '@angular/core';

import { FileWrapper } from '../common/models/file-wrapper';

@Injectable()
export class FileDownloadService {

  download(fileWrapper: FileWrapper) {
    const url = window.URL.createObjectURL(fileWrapper.file);

    const a = document.createElement('a');
    document.body.appendChild(a);
    a.setAttribute('style', 'display: none');
    a.setAttribute('target', 'blank');
    a.href = url;
    a.download = fileWrapper.fileName;
    a.click();
    window.URL.revokeObjectURL(url);
    a.remove();
  }
}
