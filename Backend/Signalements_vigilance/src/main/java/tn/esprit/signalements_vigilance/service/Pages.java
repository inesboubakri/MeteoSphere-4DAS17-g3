package tn.esprit.signalements_vigilance.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/** Petit utilitaire de pagination : taille bornee entre 1 et 100. */
final class Pages {
    private Pages() {}

    static Pageable of(int page, int size, Sort sort) {
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), sort);
    }
}
