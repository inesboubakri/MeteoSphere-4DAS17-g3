package tn.esprit.signalements_vigilance.repository;

import tn.esprit.signalements_vigilance.entity.VigilanceZone;
import tn.esprit.signalements_vigilance.enums.RiskType;
import tn.esprit.signalements_vigilance.enums.VigilanceLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VigilanceZoneRepository extends JpaRepository<VigilanceZone, Long> {

    Optional<VigilanceZone> findByZoneAndTypeRisque(String zone, RiskType typeRisque);

    List<VigilanceZone> findByTypeRisque(RiskType typeRisque);

    @Query("""
            select v from VigilanceZone v
            where (:zoneParam is null or v.zone = :zoneParam)
              and (:typeParam is null or v.typeRisque = :typeParam)
              and (:niveau is null or v.niveau = :niveau)
            """)
    Page<VigilanceZone> search(@Param("zoneParam") String zone,
                               @Param("typeParam") RiskType type,
                               @Param("niveau") VigilanceLevel niveau,
                               Pageable pageable);
}
