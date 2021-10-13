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

package es.usc.citius.servando.calendula.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;

import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.usc.citius.servando.calendula.CalendulaApp;
import es.usc.citius.servando.calendula.DailyAgendaRecyclerAdapter;
import es.usc.citius.servando.calendula.HomePagerActivity;
import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.activities.ConfirmActivity;
import es.usc.citius.servando.calendula.database.DB;
import es.usc.citius.servando.calendula.persistence.DailyScheduleItem;
import es.usc.citius.servando.calendula.persistence.HealthData;
import es.usc.citius.servando.calendula.persistence.Medicine;
import es.usc.citius.servando.calendula.persistence.Patient;
import es.usc.citius.servando.calendula.persistence.Routine;
import es.usc.citius.servando.calendula.persistence.Schedule;
import es.usc.citius.servando.calendula.persistence.ScheduleItem;
import es.usc.citius.servando.calendula.scheduling.AlarmIntentParams;
import es.usc.citius.servando.calendula.util.DailyAgendaItemStub;
import es.usc.citius.servando.calendula.util.DailyAgendaItemStub.DailyAgendaItemStubElement;
import es.usc.citius.servando.calendula.util.IconUtils;
import es.usc.citius.servando.calendula.util.LogUtil;
import es.usc.citius.servando.calendula.util.PreferenceKeys;
import es.usc.citius.servando.calendula.util.PreferenceUtils;

/**
 * Health Data fragment
 */
public class HealthDataFragment extends Fragment {

    private static final String TAG = "HealthDataFragment";
    View emptyView;

    View healthDataView;

    Patient user;

    HealthData record;

//    DailyAgendaRecyclerListener rvListener;

//    List<DailyAgendaItemStub> items = new ArrayList<>();

    IIcon emptyViewIcon = IconUtils.randomNiceIcon();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        items = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_health_data, container, false);
        healthDataView = rootView.findViewById(R.id.healthDataView);
        emptyView = rootView.findViewById(R.id.empty_view_placeholder);
//        CalendulaApp.eventBus().register(this);
        //setupRecyclerView();

        user = DB.patients().getActive(getContext());
        getNewestData();
        setupNormalView(rootView);

        setupEmptyView();

        boolean expanded = PreferenceUtils.getBoolean(PreferenceKeys.HOME_HEALTHDATA_EXPANDED, false);
        if (expanded != isExpanded()) {
            //toggleViewMode();
            ((HomePagerActivity) getActivity()).appBarLayout.setExpanded(!expanded);
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CalendulaApp.eventBus().unregister(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notifyDataChange();
    }

    public void showOrHideEmptyView(boolean show) {
        if (show) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.animate().alpha(1);
        } else {
            emptyView.setVisibility(View.GONE);
            emptyView.animate().alpha(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                }
            });
        }
    }

    public boolean isExpanded() {
        return false;
    }

    public void notifyDataChange() {
        getNewestData();
        try {
            LogUtil.d(TAG, "HealthDataView NotifyDataChange");
//            rvAdapter.notifyDataSetChanged();
            setupNormalView(getView());
            // show empty list view if there are no items
            healthDataView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOrHideEmptyView(isEmpty());
                }
            }, 100);
        } catch (Exception e) {
            LogUtil.e(TAG, "Error onPostExecute", e);
        }
    }

    public boolean isEmpty() {
        if (record == null) {
            return true;
        }
        return false;
    }

    public void getNewestData(){
        List<HealthData> healthDataList = DB.healthData().findAllForActivePatient(getContext());
        if (healthDataList.isEmpty()) {
            record = null;
        } else {
            record = healthDataList.get(healthDataList.size() - 1 );
        }
    }


    public void onUserUpdate(Patient patient) {
        user = patient;
        notifyDataChange();
    }

    private void setupNormalView(View view) {
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvWeight = view.findViewById(R.id.tvWeight);
        TextView tvBMI = view.findViewById(R.id.tvBMI);
        TextView tvCondition = view.findViewById(R.id.tvCondition);

        if (record == null) {
            return;
        }

        // Get user name
        String name = record.getPatient().getName();

        // If a name ends with "s" add the name with "'"
        if (name.substring(name.length() - 1).equalsIgnoreCase("s")) {
            tvName.setText(record.getPatient().getName() + "' Health Record");
        }

        // Else if the name ends with any other character
        else {
            tvName.setText(record.getPatient().getName() + "'s Health Record");
        }

        tvWeight.setText(Double.toString(Math.round(100 * record.getWeight()) / 100) + " kg");
        tvBMI.setText(record.getBmi());
        tvCondition.setText(record.getCondition());

        // If the health condition is of a healthy weight
        String hwstr = "Healthy Weight";
        if (record.getCondition().equals(hwstr)) {
            // Set color of text to green
            tvCondition.setTextColor(Color.parseColor("#02C418"));
        }

        else {
            // Else set it to red
            tvCondition.setTextColor(Color.parseColor("#C40202"));
        }
    }


    private void setupEmptyView() {
        int color = HomeProfileMgr.colorForCurrent(getActivity());
        Drawable icon = new IconicsDrawable(getContext())
                .icon(emptyViewIcon)
                .color(color)
                .sizeDp(90)
                .paddingDp(0);
        ((ImageView) emptyView.findViewById(R.id.imageView_ok)).setImageDrawable(icon);
    }

    private void onBackgroundChange(int color) {
        Drawable icon = new IconicsDrawable(getContext())
                .icon(emptyViewIcon)
                .color(color)
                .sizeDp(90)
                .paddingDp(0);
        ((ImageView) emptyView.findViewById(R.id.imageView_ok)).setImageDrawable(icon);
    }

}