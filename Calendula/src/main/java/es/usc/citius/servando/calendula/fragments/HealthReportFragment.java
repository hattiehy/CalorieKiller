package es.usc.citius.servando.calendula.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import es.usc.citius.servando.calendula.CalendulaApp;
import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.activities.ReminderNotification;
import es.usc.citius.servando.calendula.database.DB;
import es.usc.citius.servando.calendula.events.PersistenceEvents;
import es.usc.citius.servando.calendula.persistence.DailyIntake;
import es.usc.citius.servando.calendula.persistence.HealthData;
import es.usc.citius.servando.calendula.persistence.Routine;
import es.usc.citius.servando.calendula.scheduling.AlarmScheduler;
import es.usc.citius.servando.calendula.util.IconUtils;
import es.usc.citius.servando.calendula.util.LogUtil;

public class HealthReportFragment extends Fragment {


    private static final String TAG = "HealthReportFragment";

    OnButtonSelectedListener mButtonSelectedCallback;
    private Button btnBMIAndWeight,btnDailyIntake;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_health_report, container, false);
        btnBMIAndWeight = rootView.findViewById(R.id.btn_BMI_weight);
        btnDailyIntake = rootView.findViewById(R.id.btn_daily_intake);

        btnBMIAndWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mButtonSelectedCallback != null ) {
//                    LogUtil.d(TAG, "Click at " + r.getName());
                    mButtonSelectedCallback.onWeightAndBMISelected();
                } else {
                    LogUtil.d(TAG, "No callback set");
                }
            }
        });

        btnDailyIntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mButtonSelectedCallback != null) {
//                    LogUtil.d(TAG, "Click at " + r.getName());
                    mButtonSelectedCallback.onDailyIntakeSelected();
                } else {
                    LogUtil.d(TAG, "No callback set");
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LogUtil.d(TAG, "Activity " + activity.getClass().getName() + ", " + (activity instanceof HealthReportFragment.OnButtonSelectedListener));
        // If the container activity has implemented
        // the callback interface, set it as listener
        if (activity instanceof HealthReportFragment.OnButtonSelectedListener) {
            mButtonSelectedCallback = (HealthReportFragment.OnButtonSelectedListener) activity;
        }
    }

    public void notifyDataChange() {

    }

    @Override
    public void onStart() {
        super.onStart();
        CalendulaApp.eventBus().register(this);
    }

    @Override
    public void onStop() {
        CalendulaApp.eventBus().unregister(this);
        super.onStop();
    }

    // Method called from the event bus
    @SuppressWarnings("unused")
    @Subscribe
    public void handleActiveUserChange(final PersistenceEvents.ActiveUserChangeEvent event) {
        notifyDataChange();
    }




    // Container Activity must implement this interface
    public interface OnButtonSelectedListener {
        void onWeightAndBMISelected();

        void onDailyIntakeSelected();
    }
}