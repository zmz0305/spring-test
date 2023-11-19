package z.studio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import z.studio.model.ScanPayload;
import z.studio.rabbitmq.IRabbitMqService;
import z.studio.rabbitmq.SimpleRabbitMqService;
import z.studio.service.ScanPayloadService;
import z.studio.storage.StorageService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/scanPayload")
public class ScanPayloadController {
    private final ScanPayloadService scanPayloadService;
    private final StorageService storageService;
    private final IRabbitMqService rabbitMqService;

    @Autowired
    public ScanPayloadController(StorageService storageService, ScanPayloadService scanPayloadService, IRabbitMqService rabbitMqService) {
        this.storageService = storageService;
        this.scanPayloadService = scanPayloadService;
        this.rabbitMqService = rabbitMqService;
    }

    @GetMapping
    public List<ScanPayload> findAll() {
        return scanPayloadService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<ScanPayload> findById(@PathVariable Long id) {
        return scanPayloadService.findById(id);
    }

    @GetMapping("/listfiles")
    public String listUploadedFiles() throws IOException {
        String filePaths = storageService.loadAll().map(path -> path.getFileName().toString()).collect(Collectors.joining("\n"));
        return filePaths.isBlank() ? "No files are uploaded yet" : filePaths;
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        if (file == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @ResponseStatus(HttpStatus.CREATED) // 201
    @PostMapping(value = "/file")
    public ScanPayload uploadPayloadFile(@RequestPart("file") MultipartFile file, @RequestPart("payload") ScanPayload scanPayload) {
        storageService.store(file);
        rabbitMqService.send("uploaded file " + file.getOriginalFilename());
        return scanPayloadService.createPayload(scanPayload);
    }
}
