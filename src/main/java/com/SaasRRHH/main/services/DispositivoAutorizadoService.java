package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.DispositivoAutorizado;
import com.SaasRRHH.main.repository.DispositivoAutorizadoRepository;

import java.util.List;
import java.util.Optional;

public interface DispositivoAutorizadoService {

    List<DispositivoAutorizado> listarTodo();
    Optional<DispositivoAutorizado> buscarPorId(Long id);
    DispositivoAutorizado guardar(DispositivoAutorizado dispositivoAutorizado);
    DispositivoAutorizado actualizar(Long id, DispositivoAutorizado dispositivoAutorizado);
    void eliminar(Long id);

}
