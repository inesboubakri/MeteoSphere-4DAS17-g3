package tn.esprit.signalements_vigilance.repository;

import tn.esprit.signalements_vigilance.entity.Report;
import tn.esprit.signalements_vigilance.enums.ReportStatus;
import tn.esprit.signalements_vigilance.enums.RiskType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("""
            select r from Report r
            where (:zoneParam is null or r.zoneId = :zoneParam)
              and (:typeParam is null or r.type = :typeParam)
              and (:statut is null or r.statut = :statut)
              and (:userId is null or r.userId = :userId)
              and (:dateFrom is null or r.creeLe >= :dateFrom)
              and (:dateTo is null or r.creeLe <= :dateTo)
            """)
    Page<Report> search(@Param("zoneParam") String zone,
                        @Param("typeParam") RiskType type,
                        @Param("statut") ReportStatus statut,
                        @Param("userId") Long userId,
                        @Param("dateFrom") LocalDateTime from,
                        @Param("dateTo") LocalDateTime to,
                        Pageable pageable);

    /** candidats pour la detection de doublons */
    List<Report> findByZoneIdAndTypeAndCreeLeAfterAndStatutNotOrderByIdAsc(
            String zoneId, RiskType type, LocalDateTime since, ReportStatus statut);

    /** signalements valides recents, doublons exclus : base du calcul de vigilance */
    List<Report> findByZoneIdAndTypeAndStatutAndCreeLeAfterAndDuplicateOfIsNull(
            String zoneId, RiskType type, ReportStatus statut, LocalDateTime since);

    long countByUserIdAndStatut(Long userId, ReportStatus statut);

    long countByStatut(ReportStatus statut);

    long countByDuplicateOf(Long duplicateOf);

    @Query("select r.type, count(r) from Report r group by r.type")
    List<Object[]> countGroupedByType();

    @Query("select r.zoneId, count(r) from Report r group by r.zoneId order by count(r) desc")
    List<Object[]> countGroupedByZone(Pageable pageable);

    @Query("select r.userId, count(r) from Report r where r.statut = :statut group by r.userId order by count(r) desc")
    List<Object[]> topContributors(@Param("statut") ReportStatus statut, Pageable pageable);

    @Query("""
            select avg(r.lat), avg(r.lon) from Report r
            where r.zoneId = :zoneParam and r.statut = :statut and r.lat is not null and r.lon is not null
            """)
    List<Object[]> centroid(@Param("zoneParam") String zone, @Param("statut") ReportStatus statut);

    /** quand un signalement original est supprime, ses doublons redeviennent des signalements a part entiere */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Report r set r.duplicateOf = null where r.duplicateOf = :id")
    int releaseDuplicates(@Param("id") Long id);
}
