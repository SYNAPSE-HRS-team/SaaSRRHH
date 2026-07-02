// ============================================
// 📁 reporte-incidente.model.ts
// ============================================

import { EmpleadoResponse } from './empleado.model';
import { TareaAsignadaResponse } from './tarea-asignada.model';
import { AreaTrabajo } from './area-trabajo.model';

export enum TipoIncidente {
    ACTO_SEGURO = 'ACTO_SEGURO',
    ACTO_INSEGURO = 'ACTO_INSEGURO',
    INCIDENTE = 'INCIDENTE',
    ACCIDENTE = 'ACCIDENTE',
}

export enum NivelRiesgo {
    BAJO = 'BAJO',
    MEDIO = 'MEDIO',
    ALTO = 'ALTO',
    CRITICO = 'CRITICO',
}

export enum EstadoIncidente {
    REPORTADO = 'REPORTADO',
    EN_REVISION = 'EN_REVISION',
    CERRADO = 'CERRADO',
}

export interface ReporteIncidenteRequest {
    empleadoId: number;
    supervisorId?: number;
    tareaId?: number;
    areaId?: number;
    tipo: string;
    descripcion: string;
    evidenciaUrl?: string;
    nivelRiesgo: string;
    estado: string;
    fechaIncidente: Date | string;
}

// ✅ INTERFAZ COMPLETA CON OBJETOS ANIDADOS
export interface ReporteIncidenteResponse {
    id: number;
    
    // IDs
    empleadoId: number;
    supervisorId?: number;
    tareaId?: number;
    areaId?: number;
    
    // ✅ OBJETOS COMPLETOS (estos son los que necesita el frontend)
    empleado?: EmpleadoResponse;
    supervisor?: EmpleadoResponse;
    tarea?: TareaAsignadaResponse;
    area?: AreaTrabajo;
    
    tipo: string;
    descripcion: string;
    evidenciaUrl?: string;
    nivelRiesgo: string;
    estado: string;
    fechaIncidente: Date | string;
    fechaRegistro: Date | string;
}