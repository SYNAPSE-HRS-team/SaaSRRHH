package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.BoletaPagoResponseDTO;
import com.SaasRRHH.main.mapper.BoletaPagoMapper;
import com.SaasRRHH.main.model.BoletaPago;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.Usuario;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.repository.UsuarioRepository;
import com.SaasRRHH.main.services.BoletaPagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boletas_pago")
public class BoletaPagoController {

    private final BoletaPagoService service;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;

    public BoletaPagoController(BoletaPagoService service,
                                 UsuarioRepository usuarioRepository,
                                 EmpleadoRepository empleadoRepository) {
        this.service = service;
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @GetMapping
    public List<BoletaPagoResponseDTO> listar() {
        return service.listar().stream()
                .map(BoletaPagoMapper::toDTO)
                .toList();
    }

    @GetMapping("/mis-boletas")
    public List<BoletaPagoResponseDTO> listarMisBoletas() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
        Empleado empleado = empleadoRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("No hay un empleado asociado a este usuario"));
        return service.listarPorEmpleadoId(empleado.getId()).stream()
                .map(BoletaPagoMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoletaPago> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public BoletaPago crear(@RequestBody BoletaPago boleta) {
        return service.guardar(boleta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoletaPago> actualizar(
            @PathVariable Long id,
            @RequestBody BoletaPago data
    ) {
        if (service.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.actualizar(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (service.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}