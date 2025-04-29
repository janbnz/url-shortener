package de.janbnz.urlshortener.shortening.domain;

import de.janbnz.urlshortener.shortening.domain.model.ShortenedURL;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShorteningRepository extends JpaRepository<ShortenedURL, String> {}
