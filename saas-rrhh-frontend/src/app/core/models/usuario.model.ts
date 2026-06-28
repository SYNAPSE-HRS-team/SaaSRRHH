export interface Usuario {
  id?: number;
  email: string;
  password?: string;
  activo?: boolean;
  rol?: Rol;
  fechaCreacion?: string;
  ultimoAcceso?: string;
}
export interface UsuarioRequest {
  email: string;
  password?: string; // El '?' significa opcional. Al crear es obligatorio, pero si usas esta misma interfaz para EDITAR, la contraseña no se envía (así no la sobreescribes en blanco).
  activo?: boolean;
  rol: {
    id: number;
  };
}
export interface UsuarioResponse {
  id: number;
  email: string;
  activo: boolean;
  rol: RolResponse;
  fechaCreacion: string;
  ultimoAcceso?: string;
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
