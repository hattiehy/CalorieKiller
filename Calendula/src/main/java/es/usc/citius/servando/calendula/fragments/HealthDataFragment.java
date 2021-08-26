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

    LinearLayoutManager llm;

    View healthDataView;

    Patient user;

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
        CalendulaApp.eventBus().register(this);
        //setupRecyclerView();

        user = DB.patients().getActive(getContext());

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
            //emptyView.animate().alpha(1);
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
        try {
            LogUtil.d(TAG, "HealthDataView NotifyDataChange");
//            rvAdapter.notifyDataSetChanged();
            user = DB.patients().getActive(getContext());
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
        if (Double.isNaN(user.getWeight()) || user.getBmi() == null || user.getCondition() == null) {
            return true;
        }
        return false;
    }

    public void onUserUpdate() {
        notifyDataChange();
    }

    // Method called from the event bus
    @Subscribe
    public void handleBackgroundUpdatedEvent(final HomeProfileMgr.BackgroundUpdatedEvent event) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onBackgroundChange(HomeProfileMgr.colorForCurrent(getActivity()));
            }
        }, 500);
    }

    private void setupNormalView(View view) {
        TextView tvWeight = view.findViewById(R.id.tvWeight);
        TextView tvBMI = view.findViewById(R.id.tvBMI);
        TextView tvCondition = view.findViewById(R.id.tvCondition);

        tvWeight.setText("Current Weight: " + Double.toString(user.getWeight()));
        tvBMI.setText("BMI: " + user.getBmi());
        tvCondition.setText("Body Condition: " + user.getCondition());
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

    private void showConfirmActivity(View view, DailyAgendaItemStub item, int position) {

        Intent i = new Intent(getContext(), ConfirmActivity.class);
        i.putExtra(CalendulaApp.INTENT_EXTRA_POSITION, position);
        i.putExtra(CalendulaApp.INTENT_EXTRA_DATE, item.date.toString("dd/MM/YYYY"));

        if (item.isRoutine) {
            i.putExtra(CalendulaApp.INTENT_EXTRA_ROUTINE_ID, item.id);
        } else {
            i.putExtra(CalendulaApp.INTENT_EXTRA_SCHEDULE_ID, item.id);
            i.putExtra(CalendulaApp.INTENT_EXTRA_SCHEDULE_TIME, item.time.toString(AlarmIntentParams.TIME_FORMAT));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            View v1 = view.findViewById(R.id.patient_avatar);
            View v2 = view.findViewById(R.id.linearLayout);
            View v3 = view.findViewById(R.id.routines_list_item_name);

            if (v1 != null && v2 != null && v3 != null) {
                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        new Pair<>(v1, "avatar_transition"),
                        new Pair<>(v2, "time"),
                        new Pair<>(v3, "title")
                );
                ActivityCompat.startActivity(getActivity(), i, activityOptions.toBundle());
            } else {
                startActivity(i);
            }
        } else {
            startActivity(i);
        }
    }

    private void onBackgroundChange(int color) {
        Drawable icon = new IconicsDrawable(getContext())
                .icon(emptyViewIcon)
                .color(color)
                .sizeDp(90)
                .paddingDp(0);
        ((ImageView) emptyView.findViewById(R.id.imageView_ok)).setImageDrawable(icon);
    }

    private static class DailyAgendaItemStubComparator implements Comparator<DailyAgendaItemStub> {

        static final DailyAgendaItemStubComparator instance = new DailyAgendaItemStubComparator();

        private DailyAgendaItemStubComparator() {
        }

        @Override
        public int compare(DailyAgendaItemStub a, DailyAgendaItemStub b) {

            DateTime aT = a.date.toDateTime(a.time);
            DateTime bT = b.date.toDateTime(b.time);

            if (aT.compareTo(bT) == 0 && a.isSpacer) {
                return -1;
            } else if (aT.compareTo(bT) == 0 && b.isSpacer) {
                return 1;
            } else if (aT.compareTo(bT) == 0) {
                return a.hasEvents ? -1 : 1;
            }
            return aT.compareTo(bT);
        }
    }

}