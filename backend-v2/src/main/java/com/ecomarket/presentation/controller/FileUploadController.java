package com.ecomarket.presentation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/upload")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class FileUploadController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("error", "No se seleccionó ningún archivo");
                return ResponseEntity.badRequest().body(response);
            }

            // Validar tipo de archivo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("error", "El archivo debe ser una imagen");
                return ResponseEntity.badRequest().body(response);
            }

            // Crear directorio si no existe
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
                log.info("Directorio de uploads creado: {}", uploadDir);
            }

            // Generar nombre único para el archivo
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;

            // Guardar archivo
            Path targetLocation = Paths.get(uploadDir).resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("Imagen guardada exitosamente: {}", filename);

            response.put("filename", filename);
            response.put("url", "/ecomarket/api/images/" + filename);
            response.put("message", "Imagen subida exitosamente");

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("Error al guardar la imagen: ", e);
            response.put("error", "Error al guardar la imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/product/{productId}/image")
    public ResponseEntity<Map<String, String>> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file) {
        
        // Este endpoint se puede expandir para actualizar directamente el producto
        // Por ahora solo sube la imagen y retorna el filename
        return uploadImage(file);
    }

    @DeleteMapping("/image/{filename}")
    public ResponseEntity<Map<String, String>> deleteImage(@PathVariable String filename) {
        Map<String, String> response = new HashMap<>();
        
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            boolean deleted = Files.deleteIfExists(filePath);
            
            if (deleted) {
                log.info("Imagen eliminada: {}", filename);
                response.put("message", "Imagen eliminada exitosamente");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Imagen no encontrada");
                return ResponseEntity.notFound().build();
            }
            
        } catch (IOException e) {
            log.error("Error al eliminar la imagen: ", e);
            response.put("error", "Error al eliminar la imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
