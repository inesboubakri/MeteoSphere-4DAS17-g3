package tn.esprit.signalements_vigilance.repository;

import tn.esprit.signalements_vigilance.entity.NotificationSubscription;
import tn.esprit.signalements_vigilance.enums.RiskType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationSubscriptionRepository extends JpaRepository<NotificationSubscription, Long> {

    @Query("""
            select s from NotificationSubscription s
            where (:userId is null or s.userId = :userId)
              and (:zoneParam is null or s.zone = :zoneParam)
              and (:actif is null or s.actif = :actif)
            """)
    Page<NotificationSubscription> search(@Param("userId") Long userId,
                                          @Param("zoneParam") String zone,
                                          @Param("actif") Boolean actif,
                                          Pageable pageable);

    /** abonnements actifs concernes par un evenement (zone + type de risque, ou tous les types) */
    @Query("""
            select s from NotificationSubscription s
            where s.actif = true and s.zone = :zoneParam
              and (s.typeRisque is null or s.typeRisque = :typeParam)
            """)
    List<NotificationSubscription> findMatching(@Param("zoneParam") String zone, @Param("typeParam") RiskType type);
}
