package com.example.webscraper.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.webscraper.model.FinalResult;
import com.example.webscraper.steps.StepUtils;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Service for interacting with Google Sheets via the API.
 */
@Service
public class SheetService {
    private static final Logger log = LoggerFactory.getLogger(SheetService.class);

    @Value("${google.api.base:https://sheets.googleapis.com}")
    private String apiBase;

    /**
     * Appends the provided finals data to the specified Google Sheet.
     *
     * @param spreadsheetId Google Sheet identifier
     * @param range cell range to append to
     * @param finals rows extracted from Wikipedia
     * @param accessToken OAuth access token
     * @return JSON response from Google Sheets API
     */
    public JsonNode appendToSheet(String spreadsheetId, String range, List<FinalResult> finals, String accessToken)
            throws IOException, InterruptedException {
        String url = apiBase + "/v4/spreadsheets/" + spreadsheetId + "/values/"
                + java.net.URLEncoder.encode(range, java.nio.charset.StandardCharsets.UTF_8)
                + ":append?valueInputOption=USER_ENTERED";
        log.info("Appending {} rows to spreadsheet {}", finals.size(), spreadsheetId);
        List<List<String>> rows = finals.stream()
                .map(f -> List.of(
                        f.getYear() == null ? "" : f.getYear().toString(),
                        f.getWinner(),
                        f.getScore(),
                        f.getRunnerUp()))
                .toList();
        String body = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(Map.of("values", rows));
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + accessToken);
        return StepUtils.callAPI(url, "POST", headers, body);
    }
}
