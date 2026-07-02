package com.SaasRRHH.main.config;

import com.SaasRRHH.main.services.TareaAsignadaService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TareaScheduler {

    private final TareaAsignadaService tareaService;

    // Ejecuta a las 00:00 (medianoche) todos los días
    @Scheduled(cron = "0 0 0 * * ?")
    public void marcarTareasVencidas() {
        System.out.println("Ejecutando tarea programada: marcando tareas vencidas...");
        tareaService.marcarTareasVencidas();
        System.out.println(" Tareas vencidas actualizadas.");
    }
}