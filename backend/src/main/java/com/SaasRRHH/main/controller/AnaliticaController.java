package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.DashboardDTO;
import com.SaasRRHH.main.services.AnaliticaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analitica")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnaliticaController {

    private final AnaliticaService analiticaService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> obtenerDashboard() {

        DashboardDTO dashboard =
                analiticaService.obtenerDashboard();

        return ResponseEntity.ok(dashboard);
    }
}