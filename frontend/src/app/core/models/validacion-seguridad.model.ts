import { Usuario } from './usuario.model';

export interface ValidacionSeguridad {
  idValidacion?: number;
  tipoValidacion: string;
  codigo?: string;
  fechaExpiracion?: string;
  confirmado?: boolean;
  usuario?: Usuario;
  idUsuario?: number;
}
