package com.example.webscraper.model;

/**
 * Represents a row from the FIFA World Cup finals table.
 */
public class FinalResult {
    private Integer year;
    private String winner;
    private String score;
    private String runnerUp;

    public FinalResult(Integer year, String winner, String score, String runnerUp) {
        this.year = year;
        this.winner = winner;
        this.score = score;
        this.runnerUp = runnerUp;
    }

    public Integer getYear() {
        return year;
    }

    public String getWinner() {
        return winner;
    }

    public String getScore() {
        return score;
    }

    public String getRunnerUp() {
        return runnerUp;
    }
}
