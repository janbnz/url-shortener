package de.janbnz.url.service;

public class ShortenedURL {

    private final String originalURL, shortenedURL;
    private final int redirects;

    public ShortenedURL(String originalURL, String shortenedURL, int redirects) {
        this.originalURL = originalURL;
        this.shortenedURL = shortenedURL;
        this.redirects = redirects;
    }

    public int getRedirects() {
        return redirects;
    }

    public String getOriginalURL() {
        return originalURL;
    }

    public String getShortenedURL() {
        return shortenedURL;
    }
}