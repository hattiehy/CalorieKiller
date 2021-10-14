package es.usc.citius.servando.calendula.persistence;

import static java.util.Collections.sort;

import com.j256.ormlite.field.DatabaseField;

import org.joda.time.LocalDate;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import es.usc.citius.servando.calendula.database.DB;
import es.usc.citius.servando.calendula.util.PreferenceKeys;
import es.usc.citius.servando.calendula.util.PreferenceUtils;

public class HealthData {
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_AGE = "Age";
    public static final String COLUMN_DOB = "Dob";
    public static final String COLUMN_HEIGHT = "Height";
    public static final String COLUMN_WEIGHT = "Weight";
    public static final String COLUMN_GENDER = "Gender";
    public static final String COLUMN_BMI = "BMI";
    public static final String COLUMN_CONDITION = "Condition";
    public static final String COLUMN_DATE = "Date";
    public static final String COLUMN_RECOMINTAKE = "RecomIntake";
    public static final String COLUMN_PATIENT = "Patient";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private Long id;

    @DatabaseField(columnName = COLUMN_AGE)
    private int age;

    @DatabaseField(columnName = COLUMN_DOB)
    private String dob;

    @DatabaseField(columnName = COLUMN_HEIGHT)
    private double height;

    @DatabaseField(columnName = COLUMN_WEIGHT)
    private double weight;

    @DatabaseField(columnName = COLUMN_GENDER)
    private String gender;

    @DatabaseField(columnName = COLUMN_BMI)
    private String bmi;

    @DatabaseField(columnName = COLUMN_CONDITION)
    private String condition;

    @DatabaseField(columnName = COLUMN_DATE)
    private String date;

    @DatabaseField(columnName = COLUMN_RECOMINTAKE)
    private int recomIntake;

    @DatabaseField(columnName = COLUMN_PATIENT, foreign = true, foreignAutoRefresh = true)
    private Patient patient;

    public HealthData() {
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getRecomIntake() {
        return recomIntake;
    }

    public void setRecomIntake(int recomIntake) {
        this.recomIntake = recomIntake;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void save() {
        DB.healthData().save(this);
    }

    public HealthData(Long id, int age, String dob, double height, double weight, String gender, String bmi, String condition, String date, int recomIntake, Patient patient) {
        this.id = id;
        this.age = age;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.bmi = bmi;
        this.condition = condition;
        this.date = date;
        this.recomIntake = recomIntake;
        this.patient = patient;
    }
}
