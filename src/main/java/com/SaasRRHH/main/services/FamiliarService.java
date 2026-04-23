package com.SaasRRHH.main.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SaasRRHH.main.model.Familiar;
import com.SaasRRHH.main.repository.FamiliarRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FamiliarService {

    @Autowired
    private FamiliarRepository familiarRepository;

   
    public List<Familiar> listar() {
        return familiarRepository.findAll();
    }

    
    public Optional<Familiar> obtenerPorId(Long id) {
        return familiarRepository.findById(id);
    }

    
    public Familiar guardar(Familiar familiar) {
        return familiarRepository.save(familiar);
    }

    
    public Familiar actualizar(Long id, Familiar datos) {
        Optional<Familiar> optional = familiarRepository.findById(id);

        if (optional.isPresent()) {
            Familiar familiar = optional.get();

            familiar.setEmpleado(datos.getEmpleado());
            familiar.setParentesco(datos.getParentesco());
            familiar.setNombres(datos.getNombres());
            familiar.setDniFamiliar(datos.getDniFamiliar());
            familiar.setFechaNacimiento(datos.getFechaNacimiento());
            familiar.setEstudia(datos.getEstudia());
            familiar.setActivo(datos.getActivo());

            return familiarRepository.save(familiar);
        } else {
            throw new RuntimeException("Familiar no encontrado con id: " + id);
        }
    }


    public void eliminar(Long id) {
        if (!familiarRepository.existsById(id)) {
            throw new RuntimeException("Familiar no existe con id: " + id);
        }
        familiarRepository.deleteById(id);
    }
}