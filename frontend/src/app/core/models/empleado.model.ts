export interface EmpleadoRequest {
  usuarioId?: number;
  nombres: string;
  apellidos: string;
  dni: string;
  fotoPerfilUrl?: string;
  sueldoBase?: number;
  asignacionFamiliar?: boolean;
  fechaInicioContrato?: string;
  fechaFinContrato?: string;
  cargo?: string;
  activo?: boolean;
}

export interface EmpleadoResponse {
  id: number;
  usuarioId?: number;
  email?: string;
  nombres: string;
  apellidos: string;
  dni: string;
  fotoPerfilUrl?: string;
  sueldoBase?: number;
  asignacionFamiliar?: boolean;
  fechaInicioContrato?: string;
  fechaFinContrato?: string;
  cargo?: string;
  activo?: boolean;
  fechaRegistro?: string;
}

// Alias para compatibilidad con otros modelos que usan 'Empleado'
export type Empleado = EmpleadoResponse;
