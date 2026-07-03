package com.SaasRRHH.main.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads/documentos}")
    private String uploadDir;

    public String guardar(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        Path directorio = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(directorio);

        String nombreOriginal = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        int idx = nombreOriginal.lastIndexOf('.');
        if (idx >= 0) {
            extension = nombreOriginal.substring(idx);
        }

        String nombreArchivo = UUID.randomUUID() + extension;
        Path destino = directorio.resolve(nombreArchivo);

        Files.copy(file.getInputStream(), destino);

        return "/uploads/documentos/" + nombreArchivo;
    }
}