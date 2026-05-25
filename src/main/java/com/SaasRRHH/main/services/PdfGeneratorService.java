package com.SaasRRHH.main.services;

public interface PdfGeneratorService {
    byte[] generarBoletaPdf(Long boletaId) throws Exception;
}
