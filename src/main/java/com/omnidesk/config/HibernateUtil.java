package com.omnidesk.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Loads the settings from hibernate.cfg.xml
                sessionFactory = new Configuration().configure().buildSessionFactory();
                System.out.println("Database engine started successfully.");
            } catch (Exception e) {
                System.err.println("Database connection failed: " + e);
                throw new ExceptionInInitializerError(e);
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            System.out.println("Database engine safely shut down.");
        }
    }
}