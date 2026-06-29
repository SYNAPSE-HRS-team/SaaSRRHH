// src/app/core/models/usuario.model.ts

export interface Usuario {
  id?: number;
  email: string;
  password?: string;
  activo?: boolean;
  rol?: Rol;
  fechaCreacion?: string;
  ultimoAcceso?: string;
  // 👇 NUEVOS CAMPOS
  nombre?: string;
  apellido?: string;
  telefono?: string;
}

export interface UsuarioRequest {
  email: string;
  password?: string;
  activo?: boolean;
  rolId?: number;  
  nombre?: string;
  apellido?: string;
  telefono?: string;
}

export interface UsuarioResponse {
  id: number;
  email: string;
  activo: boolean;
  rol: RolResponse;
  fechaCreacion: string;
  ultimoAcceso?: string;
  // 👇 NUEVOS CAMPOS
  nombre: string;
  apellido: string;
  telefono: string;
  // 👇 Campo adicional para facilidad de uso
  rolNombre?: string;
}

export interface Rol {
  id?: number;
  nombre?: string;
  nombreRol?: string;
}

export interface RolResponse {
  idRol: number;
  nombre?: string;
  nombreRol?: string;
}