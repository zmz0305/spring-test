package z.studio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import z.studio.model.ScanPayload;
import z.studio.service.ScanPayloadService;
import z.studio.storage.StorageService;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/scanPayload")
public class ScanPayloadController {
    @Autowired
    ScanPayloadService scanPayloadService;
    private final StorageService storageService;

    @Autowired
    public ScanPayloadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping
    public List<ScanPayload> findAll() {
        return scanPayloadService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<ScanPayload> findById(@PathVariable Long id) {
        return scanPayloadService.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED) // 201
    @PostMapping
    public ScanPayload createScanPayload(@RequestBody ScanPayload scanPayload, @RequestParam("file") MultipartFile file) {
        storageService.store(file);
        return scanPayloadService.createPayload(scanPayload);
    }

    // how to create the ScanPayload directly from RequestParam?
    @ResponseStatus(HttpStatus.CREATED) // 201
    @PostMapping("/file")
    public String uploadPayloadFile(@RequestParam("file") MultipartFile file, @RequestParam("payload") ScanPayload scanPayload) {
        storageService.store(file);
        scanPayloadService.createPayload(scanPayload);
        return "redirect:/";
    }
}
