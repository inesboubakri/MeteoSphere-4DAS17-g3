package tn.esprit.signalements_vigilance.repository;

import tn.esprit.signalements_vigilance.entity.Badge;
import tn.esprit.signalements_vigilance.enums.BadgeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    boolean existsByUserIdAndType(Long userId, BadgeType type);

    @Query("""
            select b from Badge b
            where (:userId is null or b.userId = :userId)
              and (:typeParam is null or b.type = :typeParam)
            """)
    Page<Badge> search(@Param("userId") Long userId, @Param("typeParam") BadgeType type, Pageable pageable);
}
