package es.usc.citius.servando.calendula.persistence;

import com.j256.ormlite.field.DatabaseField;

import java.sql.Date;

import es.usc.citius.servando.calendula.database.DB;

public class DailyIntake {
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PATIENT = "Patient";
    public static final String COLUMN_DATE = "Date";
    public static final String COLUMN_INTAKE = "Intake";
    public static final String COLUMN_CARBS = "Carbs";
    public static final String COLUMN_FAT = "Fat";
    public static final String COLUMN_PROTEIN = "Protein";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private Long id;

    @DatabaseField(columnName = COLUMN_PATIENT, foreign = true, foreignAutoRefresh = true)
    private Patient patient;

    @DatabaseField(columnName = COLUMN_DATE)
    private String date;

    @DatabaseField(columnName = COLUMN_INTAKE)
    private int intake;

    @DatabaseField(columnName = COLUMN_CARBS)
    private double carbs;

    @DatabaseField(columnName = COLUMN_FAT)
    private double fat;

    @DatabaseField(columnName = COLUMN_PROTEIN)
    private double protein;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIntake() {
        return intake;
    }

    public void setIntake(int intake) {
        this.intake = intake;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public DailyIntake() {
    }

    public void save() {
        DB.dailyIntake().save(this);
    }

    public DailyIntake(Long id, Patient patient, String date, int intake, double carbs, double fat, double protein) {
        this.id = id;
        this.patient = patient;
        this.date = date;
        this.intake = intake;
        this.carbs = carbs;
        this.fat = fat;
        this.protein = protein;
    }

    @Override
    public String toString() {
        return "DailyIntake{" +
                "id=" + id +
                ", patient=" + patient +
                ", date=" + date +
                ", intake=" + intake +
                ", carbs=" + carbs +
                ", fat=" + fat +
                ", protein=" + protein +
                '}';
    }
}
