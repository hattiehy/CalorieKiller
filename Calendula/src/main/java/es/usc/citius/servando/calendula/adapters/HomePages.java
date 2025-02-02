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

package es.usc.citius.servando.calendula.adapters;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.typeface.IIcon;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.fragments.DailyAgendaFragment;
import es.usc.citius.servando.calendula.fragments.DailyIntakeFragment;
import es.usc.citius.servando.calendula.fragments.FoodGroupFragment;
import es.usc.citius.servando.calendula.fragments.HealthDataFragment;
import es.usc.citius.servando.calendula.fragments.HealthReportFragment;
import es.usc.citius.servando.calendula.fragments.MedicinesListFragment;
import es.usc.citius.servando.calendula.fragments.RoutinesListFragment;
import es.usc.citius.servando.calendula.fragments.ScheduleListFragment;

public enum HomePages {
    // attention: order is important!!!
    HOME(HealthDataFragment.class.getName(), R.string.app_name, GoogleMaterial.Icon.gmd_home),
    MEDICINES(DailyIntakeFragment.class.getName(), R.string.Counter_Title, CommunityMaterial.Icon.cmd_calendar_plus),
    ROUTINES(FoodGroupFragment.class.getName(), R.string.Nutri_Title, CommunityMaterial.Icon.cmd_food_apple),
    SCHEDULES(HealthReportFragment.class.getName(), R.string.BMI_Title, CommunityMaterial.Icon.cmd_chart_bar);

    public String className;
    public int title;
    public IIcon icon;

    HomePages(String className, int title, IIcon icon) {
        this.className = className;
        this.title = title;
        this.icon = icon;
    }

    public static HomePages getPage(final int position) throws IndexOutOfBoundsException {
        return values()[position];
    }
}
