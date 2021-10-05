package es.usc.citius.servando.calendula.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmiweight);
        lcBMIWeight = findViewById(R.id.lc_BMI_weight);
        mPatient = DB.patients().getActive(this);
        setChart();
    }

    public void getNewestData(){

    }


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

        ArrayList<Entry> BMIValues = new ArrayList<>();
        ArrayList<Entry> weightValues = new ArrayList<>();
        //X-Axis
        ArrayList<String> xLabel = new ArrayList<>();

        BMIValues.add(new Entry(1, (float) 22.6));
        xLabel.add("12/06/2021");
        BMIValues.add(new Entry(26, (float) 23.1));
        xLabel.add("08/07/2021");
        BMIValues.add(new Entry(48, (float) 22.8));
        xLabel.add("30/07/2021");
        BMIValues.add(new Entry(64, (float) 21.9));
        xLabel.add("15/08/2021");
        BMIValues.add(new Entry(103, (float) 21.3));
        xLabel.add("23/09/2021");



        weightValues.add(new Entry(1, (float) 68));
        weightValues.add(new Entry(26, (float) 72));
        weightValues.add(new Entry(48, (float) 68.9));
        weightValues.add(new Entry(64, (float) 65));
        weightValues.add(new Entry(103, (float) 64));




//        PainRecordViewModel model = new ViewModelProvider(requireActivity()).get(PainRecordViewModel.class);
//        try {
//            List<PainRecord> allRecords = model.getAllRecs().get();
//            allRecords.sort(Comparator.comparing(record -> LocalDate.parse(record.recordDate, DateTimeFormatter.ofPattern("dd-MM-yyyy")), Comparator.naturalOrder()));
//            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy")).minusDays(1);
//            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy")).plusDays(1);
//
//            for (PainRecord record : allRecords) {
//                LocalDate date = LocalDate.parse(record.recordDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//                if (date.isAfter(start) && date.isBefore(end)) {
//                    float x = date.toEpochDay() - start.toEpochDay();
//                    painValues.add(new Entry(x, record.painLevel));
//                    painNums.add(Integer.valueOf(record.painLevel).doubleValue());
//                    xLabel.add(record.recordDate);
//                }
//            }
//
//            for (PainRecord record : allRecords) {
//                LocalDate date = LocalDate.parse(record.recordDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//                if (date.isAfter(start) && date.isBefore(end)) {
//                    float x = date.toEpochDay() - start.toEpochDay();
//                    if (weather.equals("temperature")) {
//                        weatherValues.add(new Entry(x, (float) record.temperature));
//                        weathers.add(record.temperature);
//                    } else if (weather.equals("humidity")) {
//                        weatherValues.add(new Entry(x, (float) record.humidity));
//                        weathers.add(record.humidity);
//                    } else {
//                        weatherValues.add(new Entry(x, (float) record.pressure));
//                        weathers.add(record.pressure);
//                    }
//                }
//            }
//
//
//        } catch (
//                ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        LineDataSet set1, set2;
        set1 = new LineDataSet(BMIValues, "BMI");
        set2 = new LineDataSet(weightValues, "Weight: kg");

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

        XAxis xAxis = lcBMIWeight.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabel));
        xAxis.setTextColor(Color.parseColor("#333333"));
        xAxis.setTextSize(11f);
//        xAxis.setAxisMinimum(0f);
        xAxis.setLabelRotationAngle(-60);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        xAxis.setLabelCount(7);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        // set data
        lcBMIWeight.setData(data);
        lcBMIWeight.invalidate();
    }
}