import { Empleado } from './empleado.model';

export interface DocumentoPrivado {
  idDocumento?: number;
  nombre: string;
  tipoDocumento?: string;
  contenido?: string;
  fechaSubida?: string;
  empleado?: Empleado;
  idEmpleado?: number;
}

export interface TipoDocumento {
  idTipo?: number;
  nombreTipo: string;
  descripcion?: string;
}
