package z.studio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import z.studio.model.ScanPayload;
import z.studio.repository.ScanPayloadRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
public class ScanPayloadService {
    @Autowired
    ScanPayloadRepository scanPayloadRepository;

    public List<ScanPayload> findAll() {
        return scanPayloadRepository.findAll();
    }

    public Optional<ScanPayload> findById(Long id) {
        return scanPayloadRepository.findById(id);
    }

    public ScanPayload createPayload(ScanPayload scanPayload) {
        return scanPayloadRepository.save(scanPayload);
    }

    public void deleteById(Long id) {
        scanPayloadRepository.deleteById(id);
    }

    public List<ScanPayload> findScanPayloadAfterDate(LocalDate date) {
        return scanPayloadRepository.findBySubmitDateAfter(date);
    }
}
