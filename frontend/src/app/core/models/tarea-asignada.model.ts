export interface TareaAsignada {
  id: number;
  empleadoId: number;
  supervisorId: number;
  areaId: number;
  funcion: 'CULTIVADOR' | 'ROCIADOR' | 'ARADOR' | 'RECOLECTOR' | 'LIMPIADOR';
  fecha: Date;
  descripcion: string;
  estado: 'PENDIENTE' | 'EN_PROGRESO' | 'COMPLETADO' | 'CANCELADO';
  fechaRegistro: Date;
  empleado?: {
    id: number;
    nombres: string;
    apellidos: string;
    dni: string;
  };
  supervisor?: {
    id: number;
    nombres: string;
    apellidos: string;
  };
  area?: {
    id: number;
    nombre: string;
    cultivoTipo: string;
  };
}

export interface TareaAsignadaRequest {
  empleadoId: number;
  supervisorId: number;
  areaId: number;
  funcion: string;
  fecha: Date;
  descripcion: string;
  estado: string;
}

export interface TareaAsignadaResponse {
  id: number;
  empleadoId: number;
  supervisorId: number;
  areaId: number;
  funcion: string;
  fecha: Date;
  descripcion: string;
  estado: string;
  fechaRegistro: Date;

  empleado?: {
    id: number;
    nombres: string;
    apellidos: string;
    dni: string;
  };
  supervisor?: {
    id: number;
    nombres: string;
    apellidos: string;
  };
  area?: {
    id: number;
    nombre: string;
    cultivoTipo: string;
  };
}

export enum FuncionTarea {
  CULTIVADOR = 'CULTIVADOR',
  ROCIADOR = 'ROCIADOR',
  ARADOR = 'ARADOR',
  RECOLECTOR = 'RECOLECTOR',
  LIMPIADOR = 'LIMPIADOR',
}

export enum EstadoTarea {
  PENDIENTE = 'PENDIENTE',
  EN_PROGRESO = 'EN_PROGRESO',
  COMPLETADO = 'COMPLETADO',
  CANCELADO = 'CANCELADO',
  INCONCLUSO = 'INCONCLUSO',
}
