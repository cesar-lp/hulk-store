import { HttpResponse } from '@angular/common/http';

import { FileWrapper } from '../models/file-wrapper';

export const createFileWrapper = (response: HttpResponse<Blob>): FileWrapper => {
  return {
    fileName: extractFileNameFromContentDispositionHeader(response),
    file: response.body
  };
};

export const extractFileNameFromContentDispositionHeader = (response: HttpResponse<Blob>) => {
  return response.headers.get('content-disposition').split('filename=')[1];
};

