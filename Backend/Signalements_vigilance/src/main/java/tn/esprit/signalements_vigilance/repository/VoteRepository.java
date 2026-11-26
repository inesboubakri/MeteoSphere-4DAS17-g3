package tn.esprit.signalements_vigilance.repository;

import tn.esprit.signalements_vigilance.entity.Vote;
import tn.esprit.signalements_vigilance.enums.VoteValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByReportIdAndUserId(Long reportId, Long userId);

    List<Vote> findByReportIdOrderByIdAsc(Long reportId);

    long countByReportIdAndValeur(Long reportId, VoteValue valeur);

    @Query("select coalesce(sum(v.poids), 0) from Vote v where v.report.id = :reportId and v.valeur = :valeur")
    Long sumPoids(@Param("reportId") Long reportId, @Param("valeur") VoteValue valeur);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Vote v where v.report.id = :reportId")
    int deleteByReportId(@Param("reportId") Long reportId);
}
