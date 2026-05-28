package com.omnidesk.services;

import java.util.List;

import org.hibernate.Session;

import com.omnidesk.config.HibernateUtil;
import com.omnidesk.models.Transaction;

public class FinanceService {

    public void saveTransaction(Transaction transaction) {
        org.hibernate.Transaction dbTx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            dbTx = session.beginTransaction();
            session.persist(transaction);
            dbTx.commit();
        } catch (Exception e) {
            if (dbTx != null) dbTx.rollback();
            e.printStackTrace();
        }
    }

    public List<Transaction> getAllTransactions() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Fetch all transactions ordered by newest first
            return session.createQuery("from Transaction order by createdAt desc", Transaction.class).list();
        }
    }

    public double calculateTotalBalance() {
        List<Transaction> all = getAllTransactions();
        double balance = 0.0;
        for (Transaction t : all) {
            if ("INCOME".equals(t.getType())) {
                balance += t.getAmount();
            } else if ("EXPENSE".equals(t.getType())) {
                balance -= t.getAmount();
            }
        }
        return balance;
    }
}