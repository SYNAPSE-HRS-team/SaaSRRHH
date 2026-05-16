package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.DispositivoAutorizado;
import com.SaasRRHH.main.repository.DispositivoAutorizadoRepository;
import com.SaasRRHH.main.services.DispositivoAutorizadoService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
public class DispositivoAutorizadoImpl implements DispositivoAutorizadoService {

private final DispositivoAutorizadoRepository dispositivoAutorizadoRepository;
    @Override
    public List<DispositivoAutorizado> listarTodo() {
        return dispositivoAutorizadoRepository.findAll();
    }

    @Override
    public Optional<DispositivoAutorizado> buscarPorId(Long id) {
        return dispositivoAutorizadoRepository.findById(id);
    }

    @Override
    public DispositivoAutorizado guardar(DispositivoAutorizado dispositivoAutorizado) {
        return dispositivoAutorizadoRepository.save(dispositivoAutorizado);
    }

    @Override
    public DispositivoAutorizado actualizar(Long id, DispositivoAutorizado dispositivoAutorizado) {
        DispositivoAutorizado existe = dispositivoAutorizadoRepository.findById(id).orElseThrow(() -> new RuntimeException("DocumentoPrivado no encontrado"));

        actualizarDatos(existe, dispositivoAutorizado);

        return dispositivoAutorizadoRepository.save(existe);
     }

     private void actualizarDatos(DispositivoAutorizado existente, DispositivoAutorizado nuevo){
        existente.setActivo(nuevo.getActivo());
        existente.setUsuario(nuevo.getUsuario());
        existente.setFcmToken(nuevo.getFcmToken());
        existente.setHardwareId(nuevo.getHardwareId());
        existente.setFechaRegistro(nuevo.getFechaRegistro());

     }

    @Override
    public void eliminar(Long id) {

    }
}
