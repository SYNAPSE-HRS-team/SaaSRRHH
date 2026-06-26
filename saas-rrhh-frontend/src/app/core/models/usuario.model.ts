export interface Usuario {
  idUsuario?: number;
  nombreUsuario: string;
  correo: string;
  contrasena?: string;
  estado?: boolean;
  rol?: Rol;
  idRol?: number;
}

export interface UsuarioRequest {
  nombreUsuario: string;
  correo: string;
  contrasena: string;
  estado?: boolean;
  idRol: number;
}

export interface UsuarioResponse {
  idUsuario: number;
  nombreUsuario: string;
  correo: string;
  estado: boolean;
  rol: RolResponse;
}

export interface Rol {
  idRol?: number;
  nombreRol: string;
}

export interface RolResponse {
  idRol: number;
  nombreRol: string;
}
