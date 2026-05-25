package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.BoletaPago;
import com.SaasRRHH.main.repository.BoletaPagoRepository;
import com.SaasRRHH.main.services.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    private final BoletaPagoRepository boletaRepo;

    @Override
    public byte[] generarBoletaPdf(Long boletaId) throws Exception {
        BoletaPago boleta = boletaRepo.findByIdWithRelaciones(boletaId)
            .orElseThrow(() -> new IllegalArgumentException("Boleta no encontrada: " + boletaId));

        String html = renderHtml(boleta);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            byte[] pdfBytes = os.toByteArray();

            // Escribir archivo en disco y guardar ruta en la boleta
            java.nio.file.Path folder = java.nio.file.Paths.get("storage", "boletas");
            java.nio.file.Files.createDirectories(folder);
            java.nio.file.Path file = folder.resolve("boleta-" + boletaId + ".pdf");
            java.nio.file.Files.write(file, pdfBytes);

            boleta.setPdfUrl(file.toAbsolutePath().toString());
            boletaRepo.save(boleta);

            return pdfBytes;
        }
    }

    private String renderHtml(BoletaPago b) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset=\"utf-8\" /><style>");
        sb.append("body{font-family: Arial, sans-serif; font-size:12px;} .h{font-size:16px;font-weight:bold;} table{width:100%;border-collapse:collapse;} td,th{padding:6px;border:1px solid #ddd;} .r{text-align:right;}" );
        sb.append("</style></head><body>");
        sb.append("<div class='h'>Boleta de Pago</div>");
        sb.append("<p>Empleado: " + (b.getEmpleado()!=null? (b.getEmpleado().getNombres()+" "+b.getEmpleado().getApellidos()): "-") + "</p>");
        sb.append("<p>Periodo: " + (b.getPlanilla()!=null? (b.getPlanilla().getMes()+"/"+b.getPlanilla().getAnio()) : "-") + "</p>");
        sb.append("<table>");
        sb.append("<tr><th>Concepto</th><th class='r'>Monto</th></tr>");
        sb.append(row("Sueldo Base", b.getSueldoBase()!=null? b.getSueldoBase().toString():"0.00"));
        sb.append(row("Asignación Familiar", b.getAsignacionFamiliar()!=null? b.getAsignacionFamiliar().toString():"0.00"));
        sb.append(row("Bonos", b.getOtrosBonos()!=null? b.getOtrosBonos().toString():"0.00"));
        sb.append(row("Horas Extra", b.getHorasExtraPago()!=null? b.getHorasExtraPago().toString():"0.00"));
        sb.append(row("Descuento inasistencia", b.getDescuentoInasistencia()!=null? b.getDescuentoInasistencia().toString():"0.00"));
        sb.append(row("Otros descuentos", b.getOtrosDescuentos()!=null? b.getOtrosDescuentos().toString():"0.00"));
        sb.append("<tr><th>Total Neto</th><th class='r'>" + (b.getNetoPagar()!=null? b.getNetoPagar().toString():"0.00") + "</th></tr>");
        sb.append("</table>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private String row(String label, String monto) {
        return "<tr><td>" + label + "</td><td class='r'>" + monto + "</td></tr>";
    }
}
