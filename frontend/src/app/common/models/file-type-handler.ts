export class FileTypeHandler {

  static PDF = { description: 'PDF', format: 'pdf' };

  static formats: FileType[] = [
    { description: 'CSV', format: 'csv' },
    { description: 'Excel', format: 'xlsx' }
  ];
}

export interface FileType {
  description: string;
  format: string;
}
