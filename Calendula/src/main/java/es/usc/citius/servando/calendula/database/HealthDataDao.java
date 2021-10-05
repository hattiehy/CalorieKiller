package es.usc.citius.servando.calendula.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import es.usc.citius.servando.calendula.CalendulaApp;
import es.usc.citius.servando.calendula.drugdb.model.persistence.Prescription;
import es.usc.citius.servando.calendula.events.PersistenceEvents;
import es.usc.citius.servando.calendula.persistence.HealthData;
import es.usc.citius.servando.calendula.persistence.Medicine;
import es.usc.citius.servando.calendula.persistence.Patient;
import es.usc.citius.servando.calendula.persistence.PatientAlert;
import es.usc.citius.servando.calendula.persistence.PickupInfo;
import es.usc.citius.servando.calendula.persistence.Schedule;
import es.usc.citius.servando.calendula.util.alerts.AlertManager;
import es.usc.citius.servando.calendula.util.alerts.DrivingAlertHandler;
import es.usc.citius.servando.calendula.util.alerts.StockAlertHandler;

public class HealthDataDao extends GenericDao<HealthData, Long> {

    public HealthDataDao(DatabaseHelper db) {
        super(db);
    }

    @Override
    public Dao<HealthData, Long> getConcreteDao() {
        try {
            return dbHelper.getHealthDataDao();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating health data dao", e);
        }
    }

    public List<HealthData> findAllForActivePatient(Context ctx) {
        return findAll(DB.patients().getActive(ctx));
    }

    public List<HealthData> findAll(Patient p) {
        return findAll(p.getId());
    }


    public List<HealthData> findAll(Long patientId) {
        try {
            return dao.queryBuilder()
                    .where().eq(HealthData.COLUMN_PATIENT, patientId)
                    .query();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding models", e);
        }
    }

    @Override
    public void fireEvent() {
        CalendulaApp.eventBus().post(PersistenceEvents.HEALTHDATA_EVENT);
    }


    @Override
    public void save(HealthData h) {
        // save the health data
        super.save(h);

//        // save the med
//        super.save(m);
//        // generate alerts if necessary
//        StockAlertHandler.checkStockAlerts(m);
//        DrivingAlertHandler.checkDrivingAlerts(m, p);
    }

    @Override
    public void saveAndFireEvent(HealthData model) {
        save(model);
        PersistenceEvents.HModelCreateOrUpdateEvent e = new PersistenceEvents.HModelCreateOrUpdateEvent(HealthData.class);
        e.model = model;
        CalendulaApp.eventBus().post(e);

    }


    public HealthData findByPatient(Patient p) {
        try {
            QueryBuilder<HealthData, Long> qb = dao.queryBuilder();
            Where w = qb.where();
            w.eq(HealthData.COLUMN_PATIENT, p);
            qb.setWhere(w);
            return qb.queryForFirst();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding health data", e);
        }
    }

}
