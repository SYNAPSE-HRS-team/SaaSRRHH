package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.BonoDescuento;
import com.SaasRRHH.main.model.Planilla;
import com.SaasRRHH.main.services.BonoDescuentoService;
import com.SaasRRHH.main.services.NominaProcessorService;
import com.SaasRRHH.main.services.PlanillaService;
import com.SaasRRHH.main.services.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nomina")
@RequiredArgsConstructor
public class NominaController {

    private final NominaProcessorService nominaProcessor;
    private final PlanillaService planillaService;
    private final BonoDescuentoService bonoService;
    private final PdfGeneratorService pdfGenerator;

    @PostMapping("/generar")
    public ResponseEntity<Planilla> generarPlanilla(@RequestParam Integer mes, @RequestParam Integer anio) {
        Planilla p = nominaProcessor.generarPlanilla(mes, anio);
        return ResponseEntity.ok(p);
    }

    @GetMapping("/planillas")
    public ResponseEntity<List<Planilla>> listarPlanillas() {
        return ResponseEntity.ok(planillaService.listar());
    }

    @PostMapping("/planillas/{id}/cerrar")
    public ResponseEntity<Planilla> cerrarPlanilla(@PathVariable Long id) {
        try {
            Planilla cerrada = planillaService.cerrar(id);
            return ResponseEntity.ok(cerrada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/bonos")
    public ResponseEntity<BonoDescuento> crearBono(@RequestBody BonoDescuento bono) {
        BonoDescuento creado = bonoService.crear(bono);
        return ResponseEntity.ok(creado);
    }

    @GetMapping("/boleta/{id}/pdf")
    public ResponseEntity<byte[]> descargarBoletaPdf(@PathVariable("id") Long id) throws Exception {
        byte[] pdf = pdfGenerator.generarBoletaPdf(id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=boleta-"+id+".pdf")
                .body(pdf);
    }

}
