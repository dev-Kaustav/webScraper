# WebScraper (Spring Boot)

This project demonstrates how to use a set of RPA style steps to scrape the first ten rows of the "List of FIFA World Cup finals" table on Wikipedia and optionally append that data to a Google Sheet. The application exposes two HTTP endpoints for Postman testing.

## Setup

Ensure you have **Java 17** and **Maven** installed. Clone the repository and build:

```bash
mvn package
```

## Running

Start the Spring Boot application:

```bash
mvn spring-boot:run
```

### API Endpoints

- `GET /api/finals` – returns the scraped table rows as JSON.
- `POST /api/append` – scrapes and appends the rows to a Google Sheet. Provide JSON body with `spreadsheetId`, `accessToken` and optional `range` (defaults to `Sheet1!A1:D1`).

## Configuration

Application settings are defined in `src/main/resources/application.properties` and can be overridden via environment variables or command line arguments.

- `scraper.url` – Wikipedia URL to scrape
- `scraper.table.selector` – CSS selector for the table rows
- `scraper.limit` – how many rows to capture
- `google.api.base` – base URL for the Sheets API
- `sheet.default.range` – default cell range when appending

Detailed logs are emitted for each step and API call using SLF4J.

No configuration is required to read data. Provide an OAuth `accessToken` when calling `/api/append` to update a sheet.
