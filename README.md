# url-shortener
[![License: Unlicense](https://img.shields.io/badge/license-Unlicense-blue.svg)](http://unlicense.org/)

## Description

This is a simple URL shortener project built in Spring Boot using a PostgreSQL database.<br>
There is also a frontend that is made with [Svelte](https://github.com/sveltejs/svelte).

## Usage

Users are required to have an account before they can create urls. They will receive an JWT token after logging in.

## Register
```bash
curl --location 'http://localhost:8080/api/auth/register' \
--header 'Content-Type: application/json' \
--data '{"username": "YOUR_NAME", "password": "YOUR_PASSWORD"}'
```

## Login
```bash
curl --location 'http://localhost:8080/api/auth/login' \
--header 'Content-Type: application/json' \
--data '{"username": "YOUR_NAME", "password": "YOUR_PASSWORD"}'
```

### Shortening a URL

This command sends a POST request to the `/api/create` endpoint with the original URL as JSON data. The server will generate a short URL for the provided URL.

```bash
curl --location 'http://localhost:8080/api/create' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer YOUR_TOKEN_HERE' \
--data '{"url": "https://www.youtube.com/watch?v=dQw4w9WgXcQ"}'
```

The response will look like this
```json
{
    "id": "Xb1W8",
    "originalUrl": "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
    "redirectCount": 0,
    "userId": "bfc08eb7-b350-45b9-a31c-42cdd25de670"
}
```

### Retrieving URL Information

This command sends a GET request to the `/api/stats/{code}` endpoint, where `{code}` represents the short URL code. The server will return information about the URL, including the original URL and the number of redirects.
Make sure to replace `Xb1W8` with the actual short URL code.
```bash
curl --location 'http://localhost:8080/api/stats/Xb1W8'
```

The response will have the same format as the response of the `/create` endpoint above.

### Accessing Shortened URLs

Once a URL is shortened, you can access it using the shortened URL, for example:

```bash
http://localhost:8080/Xb1W8
```

## Frontend

To launch the frontend run the following command in the frontend directory
```bash
npm run dev -- --open
```

### Screenshots

![Home](https://github.com/janbnz/url-shortener/assets/23404813/7f7cccb7-6c3c-42b8-b34b-425fd7c3c3e1)

![Stats](https://github.com/janbnz/url-shortener/assets/23404813/027a5cef-9faa-400d-be12-d8cadbf150c4)

![Login](https://github.com/janbnz/url-shortener/assets/23404813/f0207b99-c65f-4b20-b6d1-695ea90f42cd)

## License

This project is licensed under the [Unlicense](http://unlicense.org/)
