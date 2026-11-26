package tn.esprit.signalements_vigilance.repository;

import tn.esprit.signalements_vigilance.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByReportIdOrderByCreeLeAsc(Long reportId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Comment c where c.parent.id = :parentId")
    int deleteRepliesOf(@Param("parentId") Long parentId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Comment c where c.report.id = :reportId and c.parent is not null")
    int deleteRepliesByReportId(@Param("reportId") Long reportId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Comment c where c.report.id = :reportId and c.parent is null")
    int deleteRootsByReportId(@Param("reportId") Long reportId);
}
