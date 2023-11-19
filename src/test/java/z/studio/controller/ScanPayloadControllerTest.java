package z.studio.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import z.studio.model.ScanPayload;
import z.studio.rabbitmq.IRabbitMqService;
import z.studio.service.ScanPayloadService;
import z.studio.storage.FileSystemStorageService;
import z.studio.storage.StorageService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

public class ScanPayloadControllerTest {
    private ScanPayloadService scanPayloadService;
    private StorageService storageService;
    private IRabbitMqService rabbitMqService;
    private ScanPayloadController scanPayloadController;

    @BeforeEach
    public void initController() {
        scanPayloadService = Mockito.mock(ScanPayloadService.class);
        storageService = Mockito.mock(StorageService.class);
        rabbitMqService = Mockito.mock(IRabbitMqService.class);
        scanPayloadController = new ScanPayloadController(storageService, scanPayloadService, rabbitMqService);
    }

    @Test
    public void testFindAll() {
        ScanPayload returnScanPayload = new ScanPayload(1L, LocalDate.of(2023, 11, 27), false);
        when(scanPayloadService.findAll()).thenReturn(Collections.singletonList(returnScanPayload));
        List<ScanPayload> res = scanPayloadController.findAll();
        Assertions.assertEquals(res.size(), 1);
        Assertions.assertEquals(res.get(0), returnScanPayload);
    }

    @Test
    public void testFindById() {
        ScanPayload returnScanPayload = new ScanPayload(1L, LocalDate.of(2023, 11, 27), false);
        when(scanPayloadService.findById(1L)).thenReturn(Optional.of(returnScanPayload));
        Optional<ScanPayload> res = scanPayloadController.findById(1L);
        Assertions.assertTrue(res.isPresent());
        Assertions.assertEquals(res.get(), returnScanPayload);
    }

    static Stream<Arguments> pathArrayProvider() {
        return Stream.of(
                Arguments.of(List.of(Path.of("./upload-dir/file1.txt"), Path.of("./upload-dir/photo1.jpg")), "file1.txt\nphoto1.jpg"),
                Arguments.of(Collections.emptyList(), "No files are uploaded yet")
        );
    }
    @ParameterizedTest
    @MethodSource("pathArrayProvider")
    public void testListUploadedFiles(List<Path> expectedPaths, String expected) throws IOException {
        when(storageService.loadAll()).thenReturn(expectedPaths.stream());
        Assertions.assertEquals(expected, scanPayloadController.listUploadedFiles());
    }

    @Test
    public void testServeFile() throws MalformedURLException {
        String filePath = "src/test/resources/mockMultipartFile.txt";
        when(storageService.loadAsResource(filePath)).thenReturn(new UrlResource(Path.of(filePath).toUri()));
        ResponseEntity<Resource> fileResource = scanPayloadController.serveFile(filePath);
        Assertions.assertEquals(fileResource.getStatusCode().value(), 200);
        Assertions.assertEquals(fileResource.getHeaders().size(), 1);
        Assertions.assertTrue(fileResource.getHeaders().containsKey("Content-Disposition"));
        Assertions.assertEquals(fileResource.getHeaders().get("Content-Disposition"),
                new ArrayList<String>(Collections.singletonList("attachment; filename=\"mockMultipartFile.txt\"")));

    }

    @Test
    public void testServeNullFile() throws MalformedURLException {
        String filePath = "src/test/resources/mockMultipartFile.txt";
        when(storageService.loadAsResource(filePath)).thenReturn(null);
        ResponseEntity<Resource> fileResource = scanPayloadController.serveFile(filePath);
        Assertions.assertEquals(fileResource.getStatusCode().value(), 404);
    }

    @Test
    public void testUploadPayloadFile() throws IOException {
        MultipartFile multipartFile = Mockito.spy(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("./mockMultipartFile.txt");
        when(multipartFile.getInputStream()).thenReturn(this.getClass().getClassLoader().getResourceAsStream("mockMultipartFile.txt"));
        ScanPayload testScanPayload = new ScanPayload(1L, LocalDate.of(2023, 11, 27), false);
        storageService = Mockito.spy(FileSystemStorageService.class);
        scanPayloadController = new ScanPayloadController(storageService, scanPayloadService, rabbitMqService);
        when(scanPayloadService.createPayload(testScanPayload)).thenReturn(testScanPayload);
        try {
            ScanPayload res = scanPayloadController.uploadPayloadFile(multipartFile, testScanPayload);
            Assertions.assertEquals(res, testScanPayload);
            Assertions.assertTrue(Files.exists(Path.of("upload-dir/mockMultipartFile.txt")));
            verify(rabbitMqService, times(1)).send("uploaded file ./mockMultipartFile.txt");
        } finally {
            Files.delete(Path.of("upload-dir/mockMultipartFile.txt"));
        }
    }
}
