package tn.esprit.signalements_vigilance.service;

import tn.esprit.signalements_vigilance.dto.PhotoDto;
import tn.esprit.signalements_vigilance.entity.Report;
import tn.esprit.signalements_vigilance.entity.ReportPhoto;
import tn.esprit.signalements_vigilance.exception.NotFoundException;
import tn.esprit.signalements_vigilance.repository.ReportPhotoRepository;
import tn.esprit.signalements_vigilance.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PhotoService {

    private final ReportPhotoRepository photos;
    private final ReportRepository reports;

    public PhotoService(ReportPhotoRepository photos, ReportRepository reports) {
        this.photos = photos;
        this.reports = reports;
    }

    public PhotoDto.Response add(Long reportId, PhotoDto.Request req) {
        Report report = reports.findById(reportId).orElseThrow(() -> NotFoundException.of("Signalement", reportId));
        ReportPhoto p = new ReportPhoto();
        p.setReport(report);
        p.setUrl(req.url());
        p.setLegende(req.legende());
        return PhotoDto.Response.from(photos.save(p));
    }

    @Transactional(readOnly = true)
    public List<PhotoDto.Response> listByReport(Long reportId) {
        requireReport(reportId);
        return photos.findByReportIdOrderByIdAsc(reportId).stream().map(PhotoDto.Response::from).toList();
    }

    @Transactional(readOnly = true)
    public PhotoDto.Response get(Long id) {
        return PhotoDto.Response.from(find(id));
    }

    public PhotoDto.Response update(Long id, PhotoDto.Request req) {
        ReportPhoto p = find(id);
        p.setUrl(req.url());
        p.setLegende(req.legende());
        return PhotoDto.Response.from(photos.save(p));
    }

    public void delete(Long id) {
        photos.delete(find(id));
    }

    private void requireReport(Long reportId) {
        if (!reports.existsById(reportId)) throw NotFoundException.of("Signalement", reportId);
    }

    private ReportPhoto find(Long id) {
        return photos.findById(id).orElseThrow(() -> NotFoundException.of("Photo", id));
    }
}
