package com.sunrise.dental.dao;

import com.sunrise.dental.dao.impl.AppointmentDAOImpl;
import com.sunrise.dental.dao.impl.BillDAOImpl;
import com.sunrise.dental.dao.impl.DentistDAOImpl;
import com.sunrise.dental.dao.impl.PatientDAOImpl;
import com.sunrise.dental.dao.impl.TreatmentDAOImpl;
import com.sunrise.dental.dao.impl.UserDAOImpl;

/**
 * Factory pattern: centralises creation of DAO implementations so callers
 * (services, servlets) depend only on the DAO interfaces, never on the
 * concrete JDBC classes. Swapping the persistence technology later only
 * means changing this one class.
 */
public final class DAOFactory {

    private DAOFactory() {
    }

    public static PatientDAO getPatientDAO() {
        return new PatientDAOImpl();
    }

    public static DentistDAO getDentistDAO() {
        return new DentistDAOImpl();
    }

    public static TreatmentDAO getTreatmentDAO() {
        return new TreatmentDAOImpl();
    }

    public static AppointmentDAO getAppointmentDAO() {
        return new AppointmentDAOImpl();
    }

    public static BillDAO getBillDAO() {
        return new BillDAOImpl();
    }

    public static UserDAO getUserDAO() {
        return new UserDAOImpl();
    }
}
