// Coincide con DocumentoPrivadoRequestDTO.java
export interface DocumentoPrivadoRequest {
  empleadoId: number;
  tipoId: number;
  archivoUrl: string;
  fechaVencimiento?: string | null; // yyyy-MM-dd
  fechaEmision?: string | null; // yyyy-MM-dd
  activo?: boolean;
}

// Coincide con DocumentoPrivadoResponseDTO.java
export interface DocumentoPrivadoResponse {
  id: number;
  empleadoId: number;
  empleadoNombre: string;
  tipoId: number;
  tipoNombre: string;
  archivoUrl: string;
  fechaVencimiento?: string | null;
  fechaEmision?: string | null; // yyyy-MM-dd
  fechaCarga: string;
  activo: boolean;
}
