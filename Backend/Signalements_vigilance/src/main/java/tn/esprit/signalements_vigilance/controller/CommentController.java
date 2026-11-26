package tn.esprit.signalements_vigilance.controller;

import tn.esprit.signalements_vigilance.dto.CommentDto;
import tn.esprit.signalements_vigilance.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@Tag(name = "Commentaires", description = "Acces direct a un commentaire (creation et liste : /reports/{id}/comments)")
public class CommentController {

    private final CommentService comments;

    public CommentController(CommentService comments) {
        this.comments = comments;
    }

    @GetMapping("/{id}")
    public CommentDto.Response get(@PathVariable Long id) {
        return comments.get(id);
    }

    @PutMapping("/{id}")
    public CommentDto.Response update(@PathVariable Long id, @Valid @RequestBody CommentDto.UpdateRequest req) {
        return comments.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        comments.delete(id);
    }
}
