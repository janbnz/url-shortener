# url-shortener
[![License: Unlicense](https://img.shields.io/badge/license-Unlicense-blue.svg)](http://unlicense.org/)

## Description

This is a simple URL shortener project built in Java. It uses Javalin to handle REST requests. The project uses a SQLite database to store information about the URLs.

## Usage

### Shortening a URL

This command sends a POST request to the `/api/create` endpoint with the original URL as JSON data. The server will generate a short URL for the provided URL.

```bash
curl --location 'http://localhost:8020/api/create' \
--header 'Content-Type: application/json' \
--data '{"url": "https://www.youtube.com/watch?v=dQw4w9WgXcQ"}'
```

The response will look like this
```json
{"url":"tQYC5b"}
```

### Retrieving URL Information

This command sends a GET request to the `/api/stats/{code}` endpoint, where `{code}` represents the short URL code. The server will return information about the URL, including the original URL and the number of redirects.
Make sure to replace `tQYC5b` with the actual short URL code.
```bash
curl --location 'http://localhost:8020/api/stats/tQYC5b'
```

The response will look like this
```json
{"original_url":"https://www.youtube.com/watch?v=dQw4w9WgXcQ","redirects":0,"shortened_url":"tQYC5b"}
```

### Accessing Shortened URLs

Once a URL is shortened, you can access it using the shortened URL, for example:

```bash
http://localhost:8020/tQYC5b
```

## Future Features

Some potential future features for this project could include implementing authorization and a web frontend.

## License

This project is licensed under the [Unlicense](http://unlicense.org/)
