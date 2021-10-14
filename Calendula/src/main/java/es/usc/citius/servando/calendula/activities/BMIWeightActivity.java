package es.usc.citius.servando.calendula.activities;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.database.DB;
import es.usc.citius.servando.calendula.persistence.HealthData;
import es.usc.citius.servando.calendula.persistence.Patient;

public class BMIWeightActivity extends AppCompatActivity {

    private LineChart lcBMIWeight;
    Patient mPatient;
    List<HealthData> healthDataList;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmiweight);
        lcBMIWeight = findViewById(R.id.lc_BMI_weight);

        mPatient = DB.patients().getActive(this);
        healthDataList = DB.healthData().findAll(mPatient);
        healthDataList.sort(Comparator.comparing(h -> LocalDate.parse(h.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy")), Comparator.naturalOrder()));

        setChart();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Entry> getBMIData(){
        ArrayList<Entry> BMIValues = new ArrayList<>();

        if (healthDataList.size() == 0){
            return BMIValues;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (HealthData healthData: healthDataList) {
                LocalDate date = LocalDate.parse(healthData.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                BMIValues.add(new Entry(date.toEpochDay(),Float.parseFloat(healthData.getBmi())));
            }
        }
        return BMIValues;
    }

    public ArrayList<Entry> getWeightData(){
        ArrayList<Entry> weightValues = new ArrayList<>();

        if (healthDataList.size() == 0){
            return weightValues;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (HealthData healthData: healthDataList) {
                LocalDate date = LocalDate.parse(healthData.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                weightValues.add(new Entry(date.toEpochDay(),(float)healthData.getWeight()));
            }
        }
        return weightValues;

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setChart() {
        lcBMIWeight.setDrawBorders(true);

        Legend legend = lcBMIWeight.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(8f);
        legend.setTextColor(this.getApplicationContext().getResources().getColor(R.color.gray_color));
        legend.setTextSize(12f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        lcBMIWeight.animateX(2500);
        lcBMIWeight.setTouchEnabled(true);
        lcBMIWeight.setDragEnabled(true);
        lcBMIWeight.setScaleEnabled(true);
        lcBMIWeight.invalidate();


        LineDataSet set1, set2;
        set1 = new LineDataSet(getBMIData(), "BMI");
        set2 = new LineDataSet(getWeightData(), "Weight: kg");


        // create a data object with the data sets
        LineData data = new LineData(set1, set2);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setCircleColor(Color.DKGRAY);
        set1.setLineWidth(2f);
        set1.setCircleRadius(3f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set2.setColor(Color.RED);
        set2.setCircleColor(Color.DKGRAY);
        set2.setLineWidth(2f);
        set2.setCircleRadius(3f);
        set2.setFillAlpha(65);
        set2.setFillColor(Color.RED);
        set2.setDrawCircleHole(false);
        set2.setHighLightColor(Color.rgb(244, 117, 117));

        data.setValueTextColor(Color.DKGRAY);
        data.setValueTextSize(9f);

        ValueFormatter xAxisFormatter = new ValueFormatter() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public String getFormattedValue(float value) {
                LocalDate date = LocalDate.ofEpochDay(Float.valueOf(value).longValue());
                return date.format(DateTimeFormatter.ofPattern("dd/MM"));
            }
        };

        XAxis xAxis = lcBMIWeight.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setTextColor(Color.parseColor("#333333"));
        xAxis.setTextSize(11f);
//        xAxis.setAxisMinimum(0f);
        xAxis.setLabelRotationAngle(-60);
        xAxis.setLabelCount(7);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = lcBMIWeight.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            lcBMIWeight.getAxisRight().setEnabled(false);

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f);

            // axis range
//            yAxis.setAxisMaximum(100f);
            yAxis.setAxisMinimum(0f);
        }

        {   // // Create Limit Lines // //
            LimitLine llXAxis = new LimitLine(5f, "Index 10");
            llXAxis.setLineWidth(2f);
            llXAxis.enableDashedLine(10f, 10f, 0f);
            llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            llXAxis.setTextSize(10f);
//            llXAxis.setTypeface(tfRegular);

            LimitLine ll1 = new LimitLine(25f, "Upper Health BMI Limit");
            ll1.setLineWidth(2f);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);
//            ll1.setTypeface(tfRegular);

            LimitLine ll2 = new LimitLine(18f, "Lower Health BMI Limit");
            ll2.setLineWidth(2f);
            ll2.enableDashedLine(10f, 10f, 0f);
            ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            ll2.setTextSize(10f);
//            ll2.setTypeface(tfRegular);

            // draw limit lines behind data instead of on top
            yAxis.setDrawLimitLinesBehindData(true);
            xAxis.setDrawLimitLinesBehindData(true);

            // add limit lines
            yAxis.addLimitLine(ll1);
            yAxis.addLimitLine(ll2);
            //xAxis.addLimitLine(llXAxis);
        }

        // set data
        lcBMIWeight.setData(data);
        lcBMIWeight.invalidate();
    }

    public void getHealthBMIData(){
        ArrayList<Entry> values1 = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            float val = (float) (Math.random() * 3) + 18;
            values1.add(new Entry(i, val));
        }

        ArrayList<Entry> values2 = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            float val = (float) (Math.random() * 3) + 20;
            values2.add(new Entry(i, val));
        }
    }
}