package model.dao;

import model.entity.*;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.Calendar;
import java.sql.Date;
import java.util.List;

/**
 * DAO class to work with table formular
 * @author Angelina Drepakova
 * @version 1.0
 */
@Stateless
public class DAOFormular implements IDAOFormular {


    /**
     * Entity manager field
     */
    @PersistenceContext(unitName = "lab4_var5_server")
    private EntityManager entityManager;

    /**
     * Constructor
     */
    public DAOFormular(){
        super();
    }

    /**
     * Method to return book
     * @param id book id
     */
    public void returnBook(int id) {
        EntityTransaction transaction;

        transaction = entityManager.getTransaction();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Formular> cq = cb.createCriteriaUpdate(Formular.class);
        Root<Formular> formularRoot = cq.from(Formular.class);
        cq.set(Formular_.return_day, cb.currentDate())
                .where(cb.equal(formularRoot.get(Formular_.book), id));

        try {
            transaction.begin();
            entityManager.createQuery(cq).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }

    }

    /**
     * Method that write reader id, date when reader took book and when he should return book
     * @param book_id book id
     * @param reader_id reader id
     */
    public void updateReaderInfo(int book_id, int reader_id) {
        EntityTransaction transaction = entityManager.getTransaction();

        java.util.Date startDate = Calendar.getInstance().getTime();
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MONTH, 6);
        java.util.Date endDate = endTime.getTime();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        Reader reader = entityManager.find(Reader.class, reader_id);

        CriteriaUpdate<Formular> criteriaUpdate = cb.createCriteriaUpdate(Formular.class);
        Root<Formular> root = criteriaUpdate.from(Formular.class);
        criteriaUpdate
                .set(root.get(Formular_.start_day), new java.sql.Date(startDate.getTime()))
                .set(root.get(Formular_.end_day), new java.sql.Date(endDate.getTime()))
                .set(root.get(Formular_.reader), reader)
                .where(cb.equal(root.get(Formular_.book), book_id));

        try {
            transaction.begin();
            entityManager.createQuery(criteriaUpdate);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    /**
     * Method to get debtors info
     * @return List of debtors
     */
    public List<Formular> getDebtors() {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Formular> cq = cb.createQuery(Formular.class);
        Root<Formular> readerRoot = cq.from(Formular.class);
        cq.where(cb.greaterThan(readerRoot.get(Formular_.return_day), readerRoot.get(Formular_.end_day)));
 

        return entityManager.createQuery(cq)
                .getResultList();
    }
}
