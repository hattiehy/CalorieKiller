package es.usc.citius.servando.calendula.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
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

import java.util.ArrayList;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.activities.SelectPicActivity;
import es.usc.citius.servando.calendula.database.DB;
import es.usc.citius.servando.calendula.persistence.Patient;


public class DailyIntakeFragment extends Fragment {

    private PieChart pcDailyIntake;
    Patient mPatient;
    int currentIntake = 0;

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
        currentIntake = SelectPicActivity.kjofAllFoods;
        setUpPieChart(currentIntake);
        return view;
    }

    public void updateIntake() {
        currentIntake += SelectPicActivity.kjofAllFoods;
        setUpPieChart(currentIntake);
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


    private void setUpPieChart(int currentIntake){
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

        int recomIntake = mPatient.getRecomIntake();
        setUpChartDate(recomIntake, currentIntake);
        int remaining = recomIntake - currentIntake;

        pcDailyIntake.setCenterText(generateCenterSpannableText(remaining));
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