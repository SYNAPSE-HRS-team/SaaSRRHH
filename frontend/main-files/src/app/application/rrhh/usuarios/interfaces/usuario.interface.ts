// src/app/application/rrhh/usuarios/interfaces/usuario.interface.ts

// Lo que ENVIAMOS al backend (para crear/actualizar)
export interface UsuarioRequest {
  email: string;
  password: string;
  role: string;
}

// Lo que RECIBIMOS del backend (respuesta)
export interface UsuarioResponse {
  id: number;
  email: string;
  role: string;
  activo: boolean;
  ultimoAcceso: string;
  fechaCreacion: string;
}
