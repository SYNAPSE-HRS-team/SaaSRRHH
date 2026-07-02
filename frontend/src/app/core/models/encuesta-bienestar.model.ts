export interface EncuestaBienestarResponse {
    id: number;
    empleadoId: number;
    fecha: string;
    cargaLaboral: number;
    apoyoEquipo: number;
    proyeccion: number;
    nombreEmpleado: string;       
    promedioGeneral: number;       
    nivelBienestar: string;      
    fechaRegistro?: string;
}

export interface EncuestaBienestarRequest {
    empleadoId: number;
    fecha: string;
    cargaLaboral: number;
    apoyoEquipo: number;
    proyeccion: number;
}