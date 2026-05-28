package com.omnidesk.services;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.omnidesk.config.HibernateUtil;
import com.omnidesk.models.Task;

public class TaskService {

    public void saveTask(Task task) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(task);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public List<Task> getAllTasks() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // HQL (Hibernate Query Language) to fetch all tasks
            return session.createQuery("from Task", Task.class).list();
        }
    }
}