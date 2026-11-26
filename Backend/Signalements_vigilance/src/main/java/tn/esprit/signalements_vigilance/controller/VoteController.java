package tn.esprit.signalements_vigilance.controller;

import tn.esprit.signalements_vigilance.dto.VoteDto;
import tn.esprit.signalements_vigilance.service.VoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/votes")
@Tag(name = "Votes", description = "Acces direct a un vote (creation et liste : /reports/{id}/votes)")
public class VoteController {

    private final VoteService votes;

    public VoteController(VoteService votes) {
        this.votes = votes;
    }

    @GetMapping("/{id}")
    public VoteDto.Response get(@PathVariable Long id) {
        return votes.get(id);
    }

    @PutMapping("/{id}")
    public VoteDto.Response update(@PathVariable Long id, @Valid @RequestBody VoteDto.UpdateRequest req) {
        return votes.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        votes.delete(id);
    }
}
