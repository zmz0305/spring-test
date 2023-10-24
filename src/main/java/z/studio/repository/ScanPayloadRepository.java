package z.studio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import z.studio.model.ScanPayload;

import java.time.LocalDate;
import java.util.List;

public interface ScanPayloadRepository extends JpaRepository<ScanPayload, Long> {

    @Query("SELECT s FROM ScanPayload s WHERE s.submitDate > :date")
    List<ScanPayload> findBySubmitDateAfter(@Param("date") LocalDate date);
}
