package com.example.webscraper.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.webscraper.model.FinalResult;
import com.example.webscraper.service.ScrapeService;
import com.example.webscraper.service.SheetService;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * REST endpoints for scraping and appending to Google Sheets.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger log = LoggerFactory.getLogger(ApiController.class);

    @Value("${sheet.default.range:Sheet1!A1:D1}")
    private String defaultRange;

    private final ScrapeService scrapeService;
    private final SheetService sheetService;

    public ApiController(ScrapeService scrapeService, SheetService sheetService) {
        this.scrapeService = scrapeService;
        this.sheetService = sheetService;
    }

    /**
     * Returns the scraped finals as JSON.
     *
     * @return list of finals from Wikipedia
     */
    @GetMapping("/finals")
    public List<FinalResult> getFinals() throws IOException {
        log.info("GET /api/finals");
        return scrapeService.scrapeFinals();
    }

    /**
     * Scrapes and appends the finals to Google Sheets.
     *
     * @param body JSON body containing `spreadsheetId`, `accessToken` and optional `range`
     * @return result from Google Sheets API
     */
    @PostMapping("/append")
    public ResponseEntity<?> append(@RequestBody Map<String, String> body) {
        String spreadsheetId = body.get("spreadsheetId");
        String accessToken = body.get("accessToken");
        String range = body.getOrDefault("range", defaultRange);
        if (spreadsheetId == null || accessToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "spreadsheetId and accessToken are required"));
        }
        try {
            log.info("Appending finals to sheet {} range {}", spreadsheetId, range);
            List<FinalResult> finals = scrapeService.scrapeFinals();
            JsonNode result = sheetService.appendToSheet(spreadsheetId, range, finals, accessToken);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to append", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
