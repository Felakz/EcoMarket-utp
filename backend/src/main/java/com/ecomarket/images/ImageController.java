package com.ecomarket.images;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageStorageService storageService;

    public ImageController(ImageStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return ResponseEntity.badRequest().body("No file provided");

        // Validate size (max 5MB)
        long maxBytes = 5 * 1024 * 1024;
        if (file.getSize() > maxBytes) return ResponseEntity.status(413).body("File too large. Max 5MB");

        // Validate content type
        String contentType = file.getContentType();
        if (contentType == null) contentType = "";
        boolean allowed = contentType.startsWith("image/") || contentType.equalsIgnoreCase("application/octet-stream");
        if (!allowed) return ResponseEntity.badRequest().body("Unsupported file type");

        String filename = storageService.store(file);
        String url = "/ecomarket/api/images/" + filename;
        return ResponseEntity.ok(java.util.Map.of("filename", filename, "url", url));
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serve(@PathVariable String filename) throws MalformedURLException, IOException {
        Path file = storageService.load(filename);
        Resource resource = new UrlResource(file.toUri());
        if (!resource.exists()) return ResponseEntity.notFound().build();
        String contentType = Files.probeContentType(file);
        if (contentType == null) contentType = "application/octet-stream";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
