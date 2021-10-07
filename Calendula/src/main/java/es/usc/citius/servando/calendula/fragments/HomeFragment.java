package es.usc.citius.servando.calendula.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.codetroopers.betterpickers.datepicker.DatePicker;
import com.ycuwq.datepicker.date.DatePickerDialogFragment;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.activities.ScheduleCreationActivity;
import es.usc.citius.servando.calendula.database.DB;
import es.usc.citius.servando.calendula.persistence.HealthData;
import es.usc.citius.servando.calendula.persistence.Medicine;
import es.usc.citius.servando.calendula.persistence.Patient;
import es.usc.citius.servando.calendula.util.LogUtil;
import es.usc.citius.servando.calendula.util.Snack;

public class HomeFragment extends Fragment {

    Patient mPatient;
    Long patientId;

    TextView etAge;
    int age;
    String dob;
    TextView etWeight;
    TextView etHeight;
    private String gender;
    private RadioGroup rgGender;
    OnHealthDataEditListener mHealthDataEditCallback;
    AssetManager assetManager;

    public static final String BMI_FILE = "BMI_Percentiles_%s.csv";
    public static final String INTAKE_FILE = "Daily_Intake_%s.csv";

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        etAge = view.findViewById(R.id.user_birthday);
        etWeight = view.findViewById(R.id.user_edit_weight);
        etHeight = view.findViewById(R.id.user_edit_height);
        rgGender = view.findViewById(R.id.user_gender);

        patientId = DB.patients().getActive(getContext()).getId();
        mPatient = DB.patients().getActive(getContext());

        assetManager = getResources().getAssets();

        if(!isNewUser()){
            HealthData healthData = DB.healthData().findByPatient(mPatient);
            etAge.setHint(healthData.getDob());
            if ("Female".equals(healthData.getGender())) {
                rgGender.check(R.id.radio_female);
            } else {
                rgGender.check(R.id.radio_male);
            }

        }

        etAge.setOnClickListener(v -> {
            com.ycuwq.datepicker.date.DatePickerDialogFragment datePickerDialogFragment = new com.ycuwq.datepicker.date.DatePickerDialogFragment();
            datePickerDialogFragment.setOnDateChooseListener(new DatePickerDialogFragment.OnDateChooseListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDateChoose(int year, int month, int day) {
                    etAge.setHint(day + "-" + month + "-" + year);
                    dob = day + "-" + month + "-" + year;
                    LocalDate dob = LocalDate.of(year, month, day);
                    age = Long.valueOf(ChronoUnit.YEARS.between(dob, LocalDate.now())).intValue();
//                    Toast.makeText(getContext(), year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
                }
            });
            FragmentManager fm = getActivity().getFragmentManager();
            datePickerDialogFragment.show(fm, "DatePickerDialogFragment");
        });

        genderPicker();
        return view;
    }

    public boolean isNewUser(){
        List<HealthData> healthDataList = DB.healthData().findAll(mPatient);
        return healthDataList.size() == 0;
    }


    private boolean isValid(String strWeight, String strHeight) {
        if (strWeight == null || strHeight == null || gender == null) {
            Toast.makeText(getContext(), "Please enter all numbers" , Toast.LENGTH_SHORT).show();
            return false;
        } else if (Double.parseDouble(strHeight) <= 0 || Double.parseDouble(strWeight) <= 0) {
            Toast.makeText(getContext(), "Please enter valid numbers" , Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public String getFileName(String filename) {
        return String.format(filename, gender);
    }

    private List<List<String>> readCSV(String fileName) {
        List<List<String>> contentList=new ArrayList<>();
        InputStream inputStream;
        BufferedReader bufferedReader;
        String line = null;
        try {
            inputStream = assetManager.open(fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                if ((line.equals("Age,P5,P85,P95") || line.equals("Age_LB,Age_UB,Energy_LB,Energy_UB")))
                    continue;
                String[] content = line.split(",");
                contentList.add(Arrays.asList(content));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentList;
    }

    public List<Double> searchPercentilesByAge(int age, List<List<String>> BMIList) {
        int lineIndex = age - 2;
        List<Double>  percentiles = new ArrayList<>();
        for (String s : BMIList.get(lineIndex)) {
            double i = Double.parseDouble(s);
            percentiles.add(i);
        }
        return percentiles;
    }

    public int searchIntakeByAge(int age, List<List<String>> intakeList) {
        int intake = 0;
        for(List<String> l : intakeList) {
            int ageLB = Integer.parseInt(l.get(0));
            int ageUB = Integer.parseInt(l.get(1));
            if (ageLB <= age && age < ageUB) {
                intake = (Integer.parseInt(l.get(2)) + Integer.parseInt(l.get(3)))/2;
            }
        }
        return intake;
    }

    public String judgeCondition(double BMI, List<Double> percentiles) {
        if (BMI < percentiles.get(1)) {
            return "Underweight";
        } else if (percentiles.get(1) <= BMI && BMI <= percentiles.get(2)) {
            return "Healthy Weight";
        } else if (percentiles.get(2) <= BMI && BMI <= percentiles.get(3)) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }


    public void genderPicker() {
        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radbtn = (RadioButton) rgGender.findViewById(checkedId);
                gender = (String) radbtn.getText();
            }
        });
    }

    public void onEdit() {
        String strHeight = etHeight.getText().toString().trim();
        String strWeight = etWeight.getText().toString().trim();
        if (gender == null) {
            Toast.makeText(getContext(), "Please enter all numbers" , Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isValid(strHeight, strWeight)) {
            return;
        }
        double height = Double.parseDouble(strHeight);
        double weight = Double.parseDouble(strWeight);
        double BMI = calculateBMI(height, weight);

        String BMIfiles = getFileName(BMI_FILE);
        List<List<String>> BMIList = readCSV(BMIfiles);
        List<Double> percentiles = searchPercentilesByAge(age, BMIList);
        String condition = judgeCondition(BMI, percentiles);

        String intakeFiles = getFileName(INTAKE_FILE);
        List<List<String>> intakeList = readCSV(intakeFiles);
        int intake = searchIntakeByAge(age, intakeList);

        Date curDate = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String date = formatter.format(curDate);

        HealthData healthData = new HealthData();
        healthData.setAge(age);
        healthData.setDob(dob);
        healthData.setHeight(height);
        healthData.setWeight(weight);
        healthData.setGender(gender);
        healthData.setBmi(Double.toString(BMI));
        healthData.setCondition(condition);
        healthData.setRecomIntake(intake);
        healthData.setDate(date);
        healthData.setPatient(mPatient);
        DB.healthData().saveAndFireEvent(healthData);
        mHealthDataEditCallback.OnHealthDataEditListener(healthData);
    }

    public double calculateBMI(double height, double weight) {
        // BMI = weight / height ^ 2
        double BMI = weight / Math.pow((height / 100), 2);
        // Retain two decimals
        return (double) Math.round(BMI * 100) / 100;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // If the container activity has implemented
        // the callback interface, set it as listener
        if (activity instanceof HomeFragment.OnHealthDataEditListener) {
            mHealthDataEditCallback = (HomeFragment.OnHealthDataEditListener) activity;
        }
//        if (activity instanceof ScheduleCreationActivity) {
//            this.showConfirmButton = false;
//        }
    }

    public interface OnHealthDataEditListener {
//        void onUserEdited(Patient p);

        void OnHealthDataEditListener(HealthData h);
    }

}