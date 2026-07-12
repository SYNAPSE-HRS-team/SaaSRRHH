export interface FeedbackAnonimo {
  id?: number;
  idFeedback?: number;
  mensaje: string;
  contenido?: string;
  categoria?: string;
  fecha?: string;
  fechaEnvio?: string;
  empleado?: any;
  idEmpleado?: number;
  // ✅ NUEVOS CAMPOS
  empleadoId?: number;
  nombreEmpleado?: string;
  esAnonimo?: boolean;
  estado?: 'PENDIENTE' | 'REVISADO' | 'NO_PROCEDE' | 'ACEPTADO' | string;
  respuesta?: string;
  fechaRespuesta?: string;
}