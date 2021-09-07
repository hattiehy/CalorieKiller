/*
 *    Calendula - An assistant for personal medication management.
 *    Copyright (C) 2014-2018 CiTIUS - University of Santiago de Compostela
 *
 *    Calendula is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.usc.citius.servando.calendula.persistence;

import android.graphics.Color;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import es.usc.citius.servando.calendula.util.AvatarMgr;

/**
 * Models an user
 */
@DatabaseTable(tableName = "Patients")
public class Patient {

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_DEFAULT = "Default";
    public static final String COLUMN_AVATAR = "Avatar";
    public static final String COLUMN_COLOR = "Color";
    public static final String COLUMN_AGE = "Age";
    public static final String COLUMN_HEIGHT = "Height";
    public static final String COLUMN_WEIGHT = "Weight";
    public static final String COLUMN_GENDER = "Gender";
    public static final String COLUMN_BMI = "BMI";
    public static final String COLUMN_CONDITION = "Condition";
    public static final String COLUMN_RECOMINTAKE = "RecomIntake";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private Long id;

    @DatabaseField(columnName = COLUMN_NAME)
    private String name;

    @DatabaseField(columnName = COLUMN_DEFAULT)
    private boolean isDefault = false;

    @DatabaseField(columnName = COLUMN_AVATAR)
    private String avatar = AvatarMgr.DEFAULT_AVATAR;


    @DatabaseField(columnName = COLUMN_COLOR)
    private int color = Color.parseColor("#3498db"); // material blue 700 1976d2

    @DatabaseField(columnName = COLUMN_AGE)
    private int age;

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

    @DatabaseField(columnName = COLUMN_RECOMINTAKE)
    private int recomIntake;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getAvatar() {
        return avatar;
    }


    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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

    //    @Override
//    public String toString() {
//        return "Patient{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", isDefault=" + isDefault +
//                ", avatar='" + avatar + '\'' +
//                ", color=" + color +
//                '}';
//    }


    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isDefault=" + isDefault +
                ", avatar='" + avatar + '\'' +
                ", color=" + color +
                ", age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                ", gender='" + gender + '\'' +
                ", bmi='" + bmi + '\'' +
                ", condition='" + condition + '\'' +
                ", recomIntake='" + recomIntake + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Patient patient = (Patient) o;

        return id != null ? id.equals(patient.id) : patient.id == null;

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
