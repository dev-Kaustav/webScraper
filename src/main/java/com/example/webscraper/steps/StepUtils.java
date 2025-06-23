package com.example.webscraper.steps;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility methods that mirror the provided RPA step descriptions.
 */
public class StepUtils {
    private static final Logger log = LoggerFactory.getLogger(StepUtils.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    /** Opens a webpage and returns a Jsoup Document. */
    public static Document openPage(String url) throws IOException {
        log.info("Opening page {}", url);
        String proxyUrl = System.getenv("https_proxy");
        if (proxyUrl == null) {
            proxyUrl = System.getenv("http_proxy");
        }
        if (proxyUrl != null) {
            try {
                URL proxy = new URL(proxyUrl);
                Proxy javaProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort()));
                return Jsoup.connect(url).proxy(javaProxy).get();
            } catch (MalformedURLException e) {
                log.warn("Invalid proxy URL: {}", proxyUrl);
            }
        }
        return Jsoup.connect(url).get();
    }

    /** Converts a string representation of a number into an Integer. */
    public static Integer parseNumber(String text) {
        log.info("Parsing number from '{}'", text);
        String digits = text.replaceAll("[^0-9-]", "");
        if (digits.isEmpty()) {
            return null;
        }
        return Integer.parseInt(digits);
    }

    /**
     * Iterates over a list, executing the consumer for each element.
     * The index starts from 1.
     */
    public static <T> void loop(List<T> list, BiConsumer<T, Integer> consumer) {
        for (int i = 0; i < list.size(); i++) {
            int index = i + 1;
            log.info("Loop iteration {}", index);
            consumer.accept(list.get(i), index);
        }
    }

    /** Extracts text from an element, optionally using a CSS selector. */
    public static String extractHTML(Element element, String selector) {
        String text = selector == null ? element.text() : element.select(selector).text();
        text = text.trim();
        log.info("Extracted text: '{}'", text);
        return text;
    }

    /** Makes an HTTP request similar to calling an API from Postman. */
    public static JsonNode callAPI(String url, String method, Map<String, String> headers, String body) throws IOException, InterruptedException {
        log.info("Calling API {} {}", method, url);
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        if (headers != null) {
            headers.forEach(builder::header);
        }
        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IOException("API request failed: " + response.statusCode() + " " + response.body());
        }
        return mapper.readTree(response.body());
    }

    /** Executes a branch if condition is true. */
    public static void detour(boolean condition, Runnable runnable) {
        log.info("Detour condition: {}", condition);
        if (condition) {
            runnable.run();
        }
    }

    // Placeholders for additional steps
    public static void fillTextField() {}
    public static void click() {}
    public static void checkBox() {}
}
