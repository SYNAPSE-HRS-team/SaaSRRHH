package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.AccesoUsuario;
import com.SaasRRHH.main.services.AccesoUsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accesos")
public class AccesoUsuarioController {

    private final AccesoUsuarioService service;

    public AccesoUsuarioController(
            AccesoUsuarioService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<List<AccesoUsuario>> listar() {

        return ResponseEntity.ok(
                service.listar()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccesoUsuario> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.buscarPorId(id)
        );
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<AccesoUsuario>> porUsuario(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                service.buscarPorUsuario(usuarioId)
        );
    }

    @PostMapping
    public ResponseEntity<AccesoUsuario> guardar(
            @RequestBody AccesoUsuario acceso) {

        AccesoUsuario nuevo =
                service.guardar(acceso);

        return new ResponseEntity<>(
                nuevo,
                HttpStatus.CREATED
        );
    }



    @PutMapping("/{id}")
    public ResponseEntity<AccesoUsuario> actualizar(
            @PathVariable Long id,
            @RequestBody AccesoUsuario acceso) {

        return ResponseEntity.ok(
                service.actualizar(
                        id,
                        acceso
                )
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }

}