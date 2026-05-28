package com.omnidesk.services;

import java.util.List;

import org.hibernate.Session;

import com.omnidesk.config.HibernateUtil;
import com.omnidesk.models.Item;

public class InventoryService {

    public void saveItem(Item item) {
        org.hibernate.Transaction dbTx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            dbTx = session.beginTransaction();
            session.persist(item);
            dbTx.commit();
        } catch (Exception e) {
            if (dbTx != null) dbTx.rollback();
            e.printStackTrace();
        }
    }

    public List<Item> getAllItems() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Item order by name asc", Item.class).list();
        }
    }
}