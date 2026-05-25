package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.DispositivoAutorizado;
import com.SaasRRHH.main.services.DispositivoAutorizadoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/dispositivo_autorizado")
@RequiredArgsConstructor
@RestController
public class DispositivoAutorizadoController {

    private final DispositivoAutorizadoService dispositivoAutorizadoService;

    @GetMapping
    public ResponseEntity<List<DispositivoAutorizado>> listarTodos() {
        return ResponseEntity.ok(dispositivoAutorizadoService.listarTodo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DispositivoAutorizado> buscarPorId(Long id) {
        return dispositivoAutorizadoService.buscarPorId(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DispositivoAutorizado> guardar(
            @Valid @RequestBody DispositivoAutorizado dispositivoAutorizado) {
        DispositivoAutorizado nuevoDispositivoAutorizado = dispositivoAutorizadoService.guardar(dispositivoAutorizado);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDispositivoAutorizado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DispositivoAutorizado> actualizar(@PathVariable Long id,
            @RequestBody DispositivoAutorizado dispositivoAutorizado) {
        try {
            DispositivoAutorizado nuevo = dispositivoAutorizadoService.actualizar(id, dispositivoAutorizado);
            return ResponseEntity.ok(dispositivoAutorizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            dispositivoAutorizadoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
