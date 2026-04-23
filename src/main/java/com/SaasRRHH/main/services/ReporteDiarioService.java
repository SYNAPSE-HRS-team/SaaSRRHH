package com.SaasRRHH.main.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SaasRRHH.main.model.ReporteDiario;
import com.SaasRRHH.main.repository.ReporteDiarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReporteDiarioService {

    @Autowired
    private ReporteDiarioRepository repository;

    
    public List<ReporteDiario> listar() {
        return repository.findAll();
    }

     
    public Optional<ReporteDiario> obtenerPorId(Long id) {
        return repository.findById(id);
    }

   
    public ReporteDiario guardar(ReporteDiario reporte) {
        if (reporte.getFechaReporte() == null) {
            reporte.setFechaReporte(LocalDateTime.now());
        }
        return repository.save(reporte);
    }

    
    public ReporteDiario actualizar(Long id, ReporteDiario datos) {
        Optional<ReporteDiario> optional = repository.findById(id);

        if (optional.isPresent()) {
            ReporteDiario r = optional.get();

            r.setTarea(datos.getTarea());
            r.setEmpleado(datos.getEmpleado());
            r.setDescripcionTrabajador(datos.getDescripcionTrabajador());
            r.setObservacionSupervisor(datos.getObservacionSupervisor());
            r.setPorcentajeAvance(datos.getPorcentajeAvance());
            r.setEstado(datos.getEstado());

            return repository.save(r);
        } else {
            throw new RuntimeException("Reporte no encontrado");
        }
    }

 
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Reporte no existe");
        }
        repository.deleteById(id);
    }
}
