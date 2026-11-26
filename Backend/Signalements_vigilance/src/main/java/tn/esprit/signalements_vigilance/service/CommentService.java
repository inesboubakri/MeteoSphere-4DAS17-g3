package tn.esprit.signalements_vigilance.service;

import tn.esprit.signalements_vigilance.dto.CommentDto;
import tn.esprit.signalements_vigilance.entity.Comment;
import tn.esprit.signalements_vigilance.entity.Report;
import tn.esprit.signalements_vigilance.exception.BadRequestException;
import tn.esprit.signalements_vigilance.exception.NotFoundException;
import tn.esprit.signalements_vigilance.repository.CommentRepository;
import tn.esprit.signalements_vigilance.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentService {

    private final CommentRepository comments;
    private final ReportRepository reports;

    public CommentService(CommentRepository comments, ReportRepository reports) {
        this.comments = comments;
        this.reports = reports;
    }

    public CommentDto.Response add(Long reportId, CommentDto.Request req) {
        Report report = reports.findById(reportId).orElseThrow(() -> NotFoundException.of("Signalement", reportId));
        Comment c = new Comment();
        c.setReport(report);
        c.setUserId(req.userId());
        c.setTexte(req.texte());
        if (req.parentId() != null) {
            Comment parent = find(req.parentId());
            if (!parent.getReport().getId().equals(reportId)) {
                throw new BadRequestException("Le commentaire parent appartient a un autre signalement");
            }
            if (parent.getParent() != null) {
                throw new BadRequestException("On ne peut repondre qu'a un commentaire racine (un seul niveau de reponses)");
            }
            c.setParent(parent);
        }
        return CommentDto.Response.from(comments.save(c));
    }

    @Transactional(readOnly = true)
    public List<CommentDto.Response> listByReport(Long reportId) {
        if (!reports.existsById(reportId)) throw NotFoundException.of("Signalement", reportId);
        return comments.findByReportIdOrderByCreeLeAsc(reportId).stream().map(CommentDto.Response::from).toList();
    }

    @Transactional(readOnly = true)
    public CommentDto.Response get(Long id) {
        return CommentDto.Response.from(find(id));
    }

    public CommentDto.Response update(Long id, CommentDto.UpdateRequest req) {
        Comment c = find(id);
        c.setTexte(req.texte());
        return CommentDto.Response.from(comments.save(c));
    }

    /** Supprime le commentaire et ses reponses. */
    public void delete(Long id) {
        Comment c = find(id);
        comments.deleteRepliesOf(id);
        comments.deleteById(c.getId());
    }

    private Comment find(Long id) {
        return comments.findById(id).orElseThrow(() -> NotFoundException.of("Commentaire", id));
    }
}
