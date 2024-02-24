package de.janbnz.url.service;

public record ShortenedURL(String originalURL, String shortenedURL, int redirects) {
}