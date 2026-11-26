package tn.esprit.signalements_vigilance.repository;

import tn.esprit.signalements_vigilance.entity.ReportPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportPhotoRepository extends JpaRepository<ReportPhoto, Long> {

    List<ReportPhoto> findByReportIdOrderByIdAsc(Long reportId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from ReportPhoto p where p.report.id = :reportId")
    int deleteByReportId(@Param("reportId") Long reportId);
}
