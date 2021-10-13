package es.usc.citius.servando.calendula.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.database.DB;
import es.usc.citius.servando.calendula.persistence.DailyIntake;
import es.usc.citius.servando.calendula.persistence.Patient;

public class DailyIntakeActivity extends AppCompatActivity {

    Patient user;
    List<DailyIntake> dailyIntakeList;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_daily_intake);

        user = DB.patients().getActive(this);
        dailyIntakeList = DB.dailyIntake().findAll(user);

        generateTables();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void generateTables(){
        ListView lv = findViewById(R.id.listView1);

        ArrayList<BarData> list = new ArrayList<>();

        if (dailyIntakeList.size() == 0) {
            list.add(generateCalorie());
            list.add(generateCarbs());
            list.add(generateFat());
            list.add(generateProtein());
        } else {
            List<BarData> cds = getData();
            for (BarData cd : cds) {
                list.add(cd);
            }
        }

        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
        lv.setAdapter(cda);
    }

    private class ChartDataAdapter extends ArrayAdapter<BarData> {

        ChartDataAdapter(Context context, List<BarData> objects) {
            super(context, 0, objects);
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            BarData data = getItem(position);

            ViewHolder holder;

            if (convertView == null) {

                holder = new ViewHolder();

                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_item_barchart, null);
                holder.chart = convertView.findViewById(R.id.chart);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // apply styling
            if (data != null) {
//                data.setValueTypeface(tfLight);
                data.setValueTextColor(Color.BLACK);
            }
            holder.chart.getDescription().setEnabled(false);
            holder.chart.setDrawGridBackground(false);

            ValueFormatter xAxisFormatter = new ValueFormatter() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public String getFormattedValue(float value) {
                    LocalDate date = LocalDate.ofEpochDay(Float.valueOf(value).longValue());
                    return date.format(DateTimeFormatter.ofPattern("dd/MM"));
                }
            };

            XAxis xAxis = holder.chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//            xAxis.setTypeface(tfLight);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f); // only intervals of 1 day
            xAxis.setLabelCount(12);
            xAxis.setValueFormatter(xAxisFormatter);

            YAxis leftAxis = holder.chart.getAxisLeft();
//            leftAxis.setTypeface(tfLight);
            leftAxis.setLabelCount(5, false);
            leftAxis.setSpaceTop(15f);

            YAxis rightAxis = holder.chart.getAxisRight();
//            rightAxis.setTypeface(tfLight);
            rightAxis.setLabelCount(5, false);
            rightAxis.setSpaceTop(15f);

            // set data
            holder.chart.setData(data);
            holder.chart.setFitBars(true);

            // do not forget to refresh the chart
//            holder.chart.invalidate();
            holder.chart.animateY(700);

            Legend l = holder.chart.getLegend();
            l.setTextSize(15f);
            l.setTextColor(Color.BLACK);
            l.setForm(Legend.LegendForm.CIRCLE);

            return convertView;
        }

        private class ViewHolder {

            BarChart chart;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<BarData> getData() {
        ArrayList<BarEntry> calories = new ArrayList<>();
        ArrayList<BarEntry> carbs = new ArrayList<>();
        ArrayList<BarEntry> fats = new ArrayList<>();
        ArrayList<BarEntry> proteins = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dailyIntakeList.sort(Comparator.comparing(dailyIntake -> LocalDate.parse(dailyIntake.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")), Comparator.naturalOrder()));
        }
        for (int i = 0; i < dailyIntakeList.size(); i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate date = LocalDate.parse(dailyIntakeList.get(i).getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                calories.add(new BarEntry(date.toEpochDay(), dailyIntakeList.get(i).getIntake()));
                carbs.add(new BarEntry(date.toEpochDay(), (float) dailyIntakeList.get(i).getCarbs()));
                fats.add(new BarEntry(date.toEpochDay(), (float) dailyIntakeList.get(i).getFat()));
                proteins.add(new BarEntry(date.toEpochDay(), (float) dailyIntakeList.get(i).getProtein()));
            }

        }

        BarDataSet d1 = new BarDataSet(calories, "Daily kilocalories records: kj");
        d1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d1.setBarShadowColor(Color.rgb(203, 203, 203));

        BarDataSet d2 = new BarDataSet(carbs, "Daily carbs records: g");
        d2.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d2.setBarShadowColor(Color.rgb(203, 203, 203));

        BarDataSet d3 = new BarDataSet(fats, "Daily fat records: g");
        d3.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d3.setBarShadowColor(Color.rgb(203, 203, 203));

        BarDataSet d4 = new BarDataSet(proteins, "Daily protein records: g");
        d4.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d4.setBarShadowColor(Color.rgb(203, 203, 203));

        ArrayList<IBarDataSet> sets1 = new ArrayList<>();
        sets1.add(d1);
        ArrayList<IBarDataSet> sets2 = new ArrayList<>();
        sets2.add(d2);
        ArrayList<IBarDataSet> sets3 = new ArrayList<>();
        sets3.add(d3);
        ArrayList<IBarDataSet> sets4 = new ArrayList<>();
        sets4.add(d4);

        BarData cd1 = new BarData(sets1);
        BarData cd2 = new BarData(sets2);
        BarData cd3 = new BarData(sets3);
        BarData cd4 = new BarData(sets4);
        cd1.setBarWidth(0.9f);
        cd2.setBarWidth(0.9f);
        cd3.setBarWidth(0.9f);
        cd4.setBarWidth(0.9f);

        List<BarData> cds = new ArrayList<>();
        cds.add(cd1);
        cds.add(cd2);
        cds.add(cd3);
        cds.add(cd4);
        return cds;
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Bar data
     */
    private BarData generateCalorie() {

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            entries.add(new BarEntry(i, (float) (Math.random() * 2000) + 6000));
        }

        BarDataSet d = new BarDataSet(entries, "kilocalories: Example data");
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setBarShadowColor(Color.rgb(203, 203, 203));

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        sets.add(d);

        BarData cd = new BarData(sets);
        cd.setBarWidth(0.9f);
        return cd;
    }

    private BarData generateCarbs() {

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            entries.add(new BarEntry(i, (float) (Math.random() * 100) + 200));
        }

        BarDataSet d = new BarDataSet(entries, "Carbs: Example data");
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setBarShadowColor(Color.rgb(203, 203, 203));

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        sets.add(d);

        BarData cd = new BarData(sets);
        cd.setBarWidth(0.9f);
        return cd;
    }

    private BarData generateFat() {

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            entries.add(new BarEntry(i, (float) (Math.random() * 20) + 35));
        }

        BarDataSet d = new BarDataSet(entries, "Fat: Example data");
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setBarShadowColor(Color.rgb(203, 203, 203));

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        sets.add(d);

        BarData cd = new BarData(sets);
        cd.setBarWidth(0.9f);
        return cd;
    }

    private BarData generateProtein() {

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            entries.add(new BarEntry(i, (float) (Math.random() * 30) + 60));
        }

        BarDataSet d = new BarDataSet(entries, "Protein: Example data");
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setBarShadowColor(Color.rgb(203, 203, 203));

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        sets.add(d);

        BarData cd = new BarData(sets);
        cd.setBarWidth(0.9f);
        return cd;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.only_github, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.viewGithub: {
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/ListViewBarChartActivity.java"));
//                startActivity(i);
//                break;
//            }
//        }
//
//        return true;
//    }
}