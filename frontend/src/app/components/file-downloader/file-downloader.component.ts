import { Component, Output, EventEmitter, Input } from '@angular/core';

import { FileType } from './../../constants/file-type';

@Component({
  selector: 'file-downloader',
  templateUrl: './file-downloader.component.html'
})
export class FileDownloaderComponent {

  @Input() formats: FileType[];
  @Output() requestDownload = new EventEmitter<FileType>();

  onRequestDownload(fileType: string) {
    this.requestDownload.emit(FileType[fileType]);
  }
}
