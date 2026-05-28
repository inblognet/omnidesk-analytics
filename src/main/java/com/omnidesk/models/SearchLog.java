package com.omnidesk.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "search_logs")
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String query;

    @Column(nullable = false)
    private String engine; // e.g., "Google" or "YouTube"

    @Column(name = "search_time", updatable = false)
    private LocalDateTime searchTime;

    @PrePersist
    protected void onCreate() {
        searchTime = LocalDateTime.now();
    }

    public SearchLog() {}

    public SearchLog(String query, String engine) {
        this.query = query;
        this.engine = engine;
    }

    // Getters
    public String getQuery() { return query; }
    public String getEngine() { return engine; }

    // A nice formatted time string for our UI
    public String getFormattedTime() {
        return searchTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }
}