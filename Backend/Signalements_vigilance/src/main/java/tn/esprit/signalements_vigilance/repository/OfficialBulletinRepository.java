package tn.esprit.signalements_vigilance.repository;

import tn.esprit.signalements_vigilance.entity.OfficialBulletin;
import tn.esprit.signalements_vigilance.enums.RiskType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OfficialBulletinRepository extends JpaRepository<OfficialBulletin, Long> {

    @Query("""
            select b from OfficialBulletin b
            where (:zoneParam is null or b.zone = :zoneParam)
              and (:typeParam is null or b.typeRisque = :typeParam)
              and (:atTime is null or (b.valideDe <= :atTime and b.valideJusqua >= :atTime))
            """)
    Page<OfficialBulletin> search(@Param("zoneParam") String zone,
                                  @Param("typeParam") RiskType type,
                                  @Param("atTime") LocalDateTime at,
                                  Pageable pageable);

    @Query("""
            select b from OfficialBulletin b
            where b.zone = :zoneParam and b.typeRisque = :typeParam
              and b.valideDe <= :atTime and b.valideJusqua >= :atTime
            """)
    List<OfficialBulletin> findActive(@Param("zoneParam") String zone,
                                      @Param("typeParam") RiskType type,
                                      @Param("atTime") LocalDateTime at);
}
