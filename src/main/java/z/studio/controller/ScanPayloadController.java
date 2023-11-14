package z.studio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.core.io.Resource;

import z.studio.model.ScanPayload;
import z.studio.service.ScanPayloadService;
import z.studio.storage.StorageService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @GetMapping("/listfiles")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(ScanPayloadController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
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
    // how to create the ScanPayload directly from RequestParam?
    @ResponseStatus(HttpStatus.CREATED) // 201
    @PostMapping(value = "/file")
    public String uploadPayloadFile(@RequestPart("file") MultipartFile file, @RequestPart("payload") ScanPayload scanPayload) {
        storageService.store(file);
        scanPayloadService.createPayload(scanPayload);
        return "redirect:/";
    }
}
