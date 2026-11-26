package tn.esprit.signalements_vigilance.controller;

import tn.esprit.signalements_vigilance.dto.PhotoDto;
import tn.esprit.signalements_vigilance.service.PhotoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/photos")
@Tag(name = "Photos", description = "Acces direct a une photo (creation et liste : /reports/{id}/photos)")
public class PhotoController {

    private final PhotoService photos;

    public PhotoController(PhotoService photos) {
        this.photos = photos;
    }

    @GetMapping("/{id}")
    public PhotoDto.Response get(@PathVariable Long id) {
        return photos.get(id);
    }

    @PutMapping("/{id}")
    public PhotoDto.Response update(@PathVariable Long id, @Valid @RequestBody PhotoDto.Request req) {
        return photos.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        photos.delete(id);
    }
}
