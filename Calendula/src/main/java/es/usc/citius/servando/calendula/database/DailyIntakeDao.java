package es.usc.citius.servando.calendula.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import es.usc.citius.servando.calendula.CalendulaApp;
import es.usc.citius.servando.calendula.events.PersistenceEvents;
import es.usc.citius.servando.calendula.persistence.DailyIntake;
import es.usc.citius.servando.calendula.persistence.HealthData;
import es.usc.citius.servando.calendula.persistence.Patient;

public class DailyIntakeDao extends GenericDao<DailyIntake, Long> {

    public DailyIntakeDao(DatabaseHelper db) {
        super(db);
    }

    @Override
    public Dao<DailyIntake, Long> getConcreteDao() {
        try {
            return dbHelper.getDailyIntakeDao();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating health data dao", e);
        }
    }

    public List<DailyIntake> findAllForActivePatient(Context ctx) {
        return findAll(DB.patients().getActive(ctx));
    }

    public List<DailyIntake> findAll(Patient p) {
        return findAll(p.getId());
    }


    public List<DailyIntake> findAll(Long patientId) {
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
        CalendulaApp.eventBus().post(PersistenceEvents.DAILYINTAKE_EVENT);
    }


    @Override
    public void save(DailyIntake d) {
        // save the daily intake
        super.save(d);

//        // save the med
//        super.save(m);
//        // generate alerts if necessary
//        StockAlertHandler.checkStockAlerts(m);
//        DrivingAlertHandler.checkDrivingAlerts(m, p);
    }

    @Override
    public void saveAndFireEvent(DailyIntake model) {
        save(model);
        PersistenceEvents.DModelCreateOrUpdateEvent e = new PersistenceEvents.DModelCreateOrUpdateEvent(DailyIntake.class);
        e.model = model;
        CalendulaApp.eventBus().post(e);

    }


    public DailyIntake findByPatient(Patient p) {
        try {
            QueryBuilder<DailyIntake, Long> qb = dao.queryBuilder();
            Where w = qb.where();
            w.eq(DailyIntake.COLUMN_PATIENT, p);
            qb.setWhere(w);
            return qb.queryForFirst();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding daily intake", e);
        }
    }
}
