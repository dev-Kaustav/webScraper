package com.example.webscraper.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.webscraper.model.FinalResult;
import com.example.webscraper.steps.StepUtils;

/**
 * Service that performs scraping using the RPA-style steps.
 */
@Service
public class ScrapeService {
    private static final Logger log = LoggerFactory.getLogger(ScrapeService.class);

    @Value("${scraper.url:https://en.wikipedia.org/wiki/List_of_FIFA_World_Cup_finals}")
    private String url;

    @Value("${scraper.table.selector:table.wikitable.sortable tbody > tr}")
    private String tableSelector;

    @Value("${scraper.limit:10}")
    private int limit;
    /**
     * Scrapes the configured number of FIFA World Cup finals from the page.
     *
     * @return list of finals extracted using {@link StepUtils}
     */
    public List<FinalResult> scrapeFinals() throws IOException {
        log.info("Scraping finals from {}", url);
        Document doc = StepUtils.openPage(url);
        Elements rows = doc.select(tableSelector)
            .stream()
            .filter(el -> !el.select("th[scope=row]").isEmpty())
            .limit(limit)
            .collect(java.util.stream.Collectors.toCollection(Elements::new));
        List<FinalResult> results = new ArrayList<>();
        StepUtils.loop(rows.stream().toList(), (Element row, Integer idx) -> {
            Elements cells = row.select("th, td");
            Integer year = StepUtils.parseNumber(StepUtils.extractHTML(cells.get(0), null));
            String winner = StepUtils.extractHTML(cells.get(1), null);
            String score = StepUtils.extractHTML(cells.get(2), null);
            String runnerUp = StepUtils.extractHTML(cells.get(3), null);
            results.add(new FinalResult(year, winner, score, runnerUp));
        });
        return results;
    }
}
