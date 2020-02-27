import { Component, Output, EventEmitter, Input } from '@angular/core';

import { FileType } from '../../common/models/file-type-handler';

@Component({
  selector: 'file-downloader',
  templateUrl: './file-downloader.component.html'
})
export class FileDownloaderComponent {

  @Input() formats: FileType[];

  @Output() requestDownload = new EventEmitter<FileType>();

  onRequestDownload(fileType: FileType) {
    this.requestDownload.emit(fileType);
  }
}
