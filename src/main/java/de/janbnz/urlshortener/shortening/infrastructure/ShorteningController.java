package de.janbnz.urlshortener.shortening.infrastructure;

import de.janbnz.urlshortener.shortening.domain.ShorteningService;
import de.janbnz.urlshortener.shortening.domain.model.ShortenedURL;
import de.janbnz.urlshortener.shortening.domain.model.ShorteningRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class ShorteningController {

    private final ShorteningService shorteningService;

    @PostMapping(value = "/api/create",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE
            })
    public ShortenedURL shorten(@RequestBody ShorteningRequest request) {
        return shorteningService.shorten(request.getUrl());
    }

    @GetMapping("/api/stats/{id}")
    public ShortenedURL getStats(@PathVariable String id) {
        return shorteningService.getInformation(id);
    }

    @GetMapping("/{id}")
    public RedirectView redirect(@PathVariable String id) {
        ShortenedURL url = shorteningService.redirect(id);
        String originalUrl = url.getOriginalUrl();

        RedirectView view = new RedirectView(originalUrl);
        view.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return view;
    }
}
