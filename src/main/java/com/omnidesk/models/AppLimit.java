package com.omnidesk.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_limits")
public class AppLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String appName; // e.g., "chrome.exe" or "discord.exe"

    @Column(nullable = false)
    private Integer timeLimitMinutes;

    @Column(nullable = false)
    private Integer timeSpentToday;

    public AppLimit() {}

    public AppLimit(String appName, Integer timeLimitMinutes) {
        this.appName = appName;
        this.timeLimitMinutes = timeLimitMinutes;
        this.timeSpentToday = 0;
    }

    // Getters and Setters...
    public String getAppName() { return appName; }
    public Integer getTimeLimitMinutes() { return timeLimitMinutes; }
    public Integer getTimeSpentToday() { return timeSpentToday; }
    public void setTimeSpentToday(Integer timeSpentToday) { this.timeSpentToday = timeSpentToday; }
}