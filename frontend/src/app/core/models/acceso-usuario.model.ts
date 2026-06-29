import { Usuario } from './usuario.model';

export interface AccesoUsuario {
  idAcceso?: number;
  fechaAcceso: string;
  direccionIp?: string;
  accion: string;
  usuario?: Usuario;
  idUsuario?: number;
}
