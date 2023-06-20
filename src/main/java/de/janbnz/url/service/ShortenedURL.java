package de.janbnz.url.service;

public record ShortenedURL(String originalURL, String shortenedURL, int redirects) {
    public String getOriginalURL() {
        return originalURL;
    }

    public int getRedirects() {
        return redirects;
    }
}