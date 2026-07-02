export interface MetricaBurnoutResponse {
    id: number;
    empleadoId: number;
    nombreEmpleado?: string;
    nivelRiesgo: 'BAJO' | 'MEDIO' | 'ALTO';
    horasExtraAcumuladas: number;
    tendenciaTardanza: boolean;
    fechaEvaluacion: string;
}

export interface MetricaBurnoutRequest {
    empleadoId: number;
    nivelRiesgo: 'BAJO' | 'MEDIO' | 'ALTO';
    horasExtraAcumuladas: number;
    tendenciaTardanza: boolean;
}