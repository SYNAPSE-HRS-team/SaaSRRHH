package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.Familiar;
import com.SaasRRHH.main.repository.FamiliarRepository;
import com.SaasRRHH.main.services.FamiliarService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FamiliarServiceImpl implements FamiliarService {

    private final FamiliarRepository familiarRepository;

    @Override
    public List<Familiar> listar() {
        return familiarRepository.findAll();
    }

    @Override
    public Optional<Familiar> buscarPorId(Long id) {
        return familiarRepository.findById(id);
    }

    @Override
    public Familiar guardar(Familiar familiar) {

        return familiarRepository.save(familiar);
    }

    @Override
    public Familiar actualizar(Long id, Familiar familiar) {

        Familiar existente = familiarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Familiar no encontrado"));

        actualizarDatos(existente, familiar);

        return familiarRepository.save(existente);
    }

    private void actualizarDatos(Familiar existente, Familiar nuevo) {

        existente.setEmpleado(nuevo.getEmpleado());
        existente.setParentesco(nuevo.getParentesco());
        existente.setNombres(nuevo.getNombres());
        existente.setDniFamiliar(nuevo.getDniFamiliar());
        existente.setFechaNacimiento(nuevo.getFechaNacimiento());
        existente.setEstudia(nuevo.getEstudia());
        existente.setActivo(nuevo.getActivo());
    }

    @Override
    public void eliminar(Long id) {

        familiarRepository.deleteById(id);
    }
}