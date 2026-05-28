package com.omnidesk.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.omnidesk.config.HibernateUtil;
import com.omnidesk.models.AppLimit;
import com.omnidesk.models.DailyUsage;
import com.omnidesk.models.SearchLog;
import com.omnidesk.utils.ActiveWindowTracker;
import com.omnidesk.utils.NotificationUtil;

public class ActivityMonitorService {

    // FIXED: Changed from private to public static so SystemTrayManager can see it globally
    public static boolean isRunning = false;
    public static Map<String, Integer> todaysUsage = new HashMap<>();

    private String lastRecordedSearch = "";

    public void startTracking() {
        // Since we are changing tracking state, verify bounds against static context
        if (isRunning) return;
        isRunning = true;

        System.out.println("Starting OS Activity & Search Tracker...");

        loadTodaysUsageFromDB();

        Thread trackerThread = new Thread(() -> {
            while (isRunning) {
                try {
                    String rawTitle = ActiveWindowTracker.getRawActiveWindowTitle();
                    String currentApp = ActiveWindowTracker.simplifyTitle(rawTitle);

                    if (!currentApp.equals("Unknown")) {
                        int newTotal = todaysUsage.getOrDefault(currentApp, 0) + 5;
                        todaysUsage.put(currentApp, newTotal);
                        checkLimitsAndSave(currentApp, newTotal);

                        extractAndSaveSearch(rawTitle);
                    }

                    Thread.sleep(5000);

                } catch (InterruptedException e) {
                    System.out.println("Tracker stopped.");
                }
            }
        });

        trackerThread.setDaemon(true);
        trackerThread.start();
    }

    private void extractAndSaveSearch(String rawTitle) {
        String query = null;
        String engine = null;

        if (rawTitle.contains("- Google Search")) {
            query = rawTitle.split("- Google Search")[0].trim();
            engine = "Google";
        } else if (rawTitle.contains("- YouTube")) {
            query = rawTitle.split("- YouTube")[0].trim();
            engine = "YouTube";
        }

        if (query != null && !query.isEmpty() && !query.equals(lastRecordedSearch)) {
            lastRecordedSearch = query;

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                SearchLog log = new SearchLog(query, engine);
                session.persist(log);
                tx.commit();
                System.out.println("New Search Logged: " + query);
            } catch (Exception e) {
                System.err.println("Failed to save search log.");
            }
        }
    }

    private void loadTodaysUsageFromDB() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<DailyUsage> usages = session.createQuery("from DailyUsage where usageDate = :today", DailyUsage.class)
                                             .setParameter("today", LocalDate.now())
                                             .list();
            for (DailyUsage u : usages) {
                todaysUsage.put(u.getAppName(), u.getSecondsUsed());
            }
        } catch (Exception e) {
            System.out.println("Could not load usage data.");
        }
    }

    private void checkLimitsAndSave(String appName, int secondsUsed) {
        if (secondsUsed > 0 && secondsUsed % 60 == 0) {

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();

                DailyUsage usage = session.createQuery("from DailyUsage where appName = :name and usageDate = :today", DailyUsage.class)
                                          .setParameter("name", appName)
                                          .setParameter("today", LocalDate.now())
                                          .uniqueResult();
                if (usage == null) {
                    usage = new DailyUsage(appName, LocalDate.now(), secondsUsed);
                    session.persist(usage);
                } else {
                    usage.setSecondsUsed(secondsUsed);
                    session.merge(usage);
                }

                AppLimit limit = session.createQuery("from AppLimit where appName = :name", AppLimit.class)
                                        .setParameter("name", appName)
                                        .uniqueResult();

                if (limit != null) {
                    int minutesUsed = secondsUsed / 60;
                    if (minutesUsed == limit.getTimeLimitMinutes()) {
                        NotificationUtil.sendWarningNotification(appName, limit.getTimeLimitMinutes());
                    }
                }

                tx.commit();
            }
        }
    }
}