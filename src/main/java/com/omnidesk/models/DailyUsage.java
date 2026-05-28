package com.omnidesk.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "daily_usage")
public class DailyUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String appName;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    @Column(name = "seconds_used", nullable = false)
    private Integer secondsUsed;

    public DailyUsage() {}

    public DailyUsage(String appName, LocalDate usageDate, Integer secondsUsed) {
        this.appName = appName;
        this.usageDate = usageDate;
        this.secondsUsed = secondsUsed;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getAppName() { return appName; }
    public LocalDate getUsageDate() { return usageDate; }
    public Integer getSecondsUsed() { return secondsUsed; }
    public void setSecondsUsed(Integer secondsUsed) { this.secondsUsed = secondsUsed; }
}