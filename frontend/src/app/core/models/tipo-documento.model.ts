// Coincide con TipoDocumentoRequestDTO.java
export interface TipoDocumentoRequest {
  nombre: string;
  obligatorio?: boolean;
  diasVigencia?: number | null;
  requiereRenovacion?: boolean;
  descripcion?: string;
}

// Coincide con TipoDocumentoResponseDTO.java
export interface TipoDocumentoResponse {
  idTipo: number;
  nombre: string;
  obligatorio: boolean;
  diasVigencia?: number | null;
  requiereRenovacion: boolean;
  descripcion?: string;
}