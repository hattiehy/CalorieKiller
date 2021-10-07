package es.usc.citius.servando.calendula.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.activities.SelectPicActivity;
import es.usc.citius.servando.calendula.database.DB;
import es.usc.citius.servando.calendula.database.DailyIntakeDao;
import es.usc.citius.servando.calendula.persistence.DailyIntake;
import es.usc.citius.servando.calendula.persistence.HealthData;
import es.usc.citius.servando.calendula.persistence.Patient;
import es.usc.citius.servando.calendula.util.LogUtil;


public class DailyIntakeFragment extends Fragment {

    private PieChart pcDailyIntake;
    Patient mPatient;

    public DailyIntakeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_intake, container, false);
        pcDailyIntake = view.findViewById(R.id.pc_daily_intake);
        mPatient = DB.patients().getActive(getContext());
        List<DailyIntake> intakeList = DB.dailyIntake().findAll(mPatient);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intakeList.sort(Comparator.comparing(dailyIntake -> LocalDate.parse(dailyIntake.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")), Comparator.naturalOrder()));
        }
        DailyIntake intake;
        if (intakeList.size() == 0) {
            intake = null;
        } else {
            intake = intakeList.get(intakeList.size() - 1);
        }
        setUpPieChart(intake);
        return view;
    }

    public void notifyDataChange() {
        setUpPieChart(DB.dailyIntake().findByPatient(mPatient));
    }


    public void onUserUpdate(Patient patient) {
        mPatient = patient;
        notifyDataChange();
    }

    private SpannableString generateCenterSpannableText(int remaining) {
        String text;
        int startIndex = Integer.toString(remaining).length();
        if (0<= remaining) {
            text = remaining + "\nKILOJOULE REMAINING";
        } else {
            text = Math.abs(remaining) + "\nKILOJOULE OVER";
        }
        SpannableString s = new SpannableString(text);
        s.setSpan(new RelativeSizeSpan(3.5f), 0, startIndex, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), startIndex+1, s.length(), 0);
        return s;
    }


    private void setUpPieChart(DailyIntake dailyIntake){
        pcDailyIntake.setUsePercentValues(false);
        pcDailyIntake.getDescription().setEnabled(false);
        pcDailyIntake.setExtraOffsets(5, 10, 5, 5);

        pcDailyIntake.setDragDecelerationFrictionCoef(0.95f);

        pcDailyIntake.setDrawHoleEnabled(true);
        pcDailyIntake.setHoleColor(Color.WHITE);

        pcDailyIntake.setTransparentCircleColor(Color.WHITE);
        pcDailyIntake.setTransparentCircleAlpha(110);

        pcDailyIntake.setHoleRadius(58f);
        pcDailyIntake.setTransparentCircleRadius(61f);

        pcDailyIntake.setDrawCenterText(true);

        pcDailyIntake.setRotationAngle(0);
        // enable rotation of the pcDailyIntake by touch
        pcDailyIntake.setRotationEnabled(true);
        pcDailyIntake.setHighlightPerTapEnabled(true);

        // add a selection listener
//        pcDailyIntake.setOnpcDailyIntakeValueSelectedListener(this);

        pcDailyIntake.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = pcDailyIntake.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        HealthData healthData = getNewestHealthData();
        int remaining;
        int currentIntake = getCurrentIntake(dailyIntake);
        int recomIntake;
        if (healthData == null) {
            recomIntake = 0;
        } else {
            recomIntake = healthData.getRecomIntake();
        }
        setUpChartDate(recomIntake, currentIntake);
        remaining = recomIntake - currentIntake;
        pcDailyIntake.setCenterText(generateCenterSpannableText(remaining));
    }

    public HealthData getNewestHealthData(){
        List<HealthData> healthDataList = DB.healthData().findAllForActivePatient(getContext());
        HealthData healthData;
        if (healthDataList.isEmpty()) {
            healthData = null;
        } else {
            healthData = healthDataList.get(healthDataList.size() - 1 );
        }
        return healthData;
    }

    public int getCurrentIntake(DailyIntake dailyIntake){
        int currentIntake;
        if (dailyIntake == null) {
            currentIntake = 0;
        } else {
            currentIntake = dailyIntake.getIntake();
        }
        return currentIntake;
    }

    private void setUpChartDate(int recomIntake, int currentIntake){
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (recomIntake > currentIntake) {
            entries.add(new PieEntry(currentIntake, "Current intake"));
            entries.add(new PieEntry(recomIntake - currentIntake, "Remaining"));
        } else {
            entries.add(new PieEntry(currentIntake, "Current intake"));
//            entries.add(new PieEntry(recomIntake, "Remaining"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Daily intake  Unit:kj");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);
        pcDailyIntake.setDrawEntryLabels(true);
        pcDailyIntake.setEntryLabelColor(Color.DKGRAY);
        pcDailyIntake.setData(data);

        // undo all highlights
        pcDailyIntake.highlightValues(null);

        pcDailyIntake.invalidate();
    }
}