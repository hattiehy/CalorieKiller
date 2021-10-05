package es.usc.citius.servando.calendula.fragments;

import android.app.Activity;
import android.content.res.AssetManager;
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

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.activities.ScheduleCreationActivity;
import es.usc.citius.servando.calendula.database.DB;
import es.usc.citius.servando.calendula.persistence.Medicine;
import es.usc.citius.servando.calendula.persistence.Patient;
import es.usc.citius.servando.calendula.util.LogUtil;
import es.usc.citius.servando.calendula.util.Snack;

public class HomeFragment extends Fragment {

    Patient mPatient;
    Long patientId;

    TextView etAge;
    TextView etWeight;
    TextView etHeight;
    private String gender;
    private RadioGroup rgGender;
    OnUserEditListener mUserEditCallback;
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

        etAge = view.findViewById(R.id.user_edit_age);
        etWeight = view.findViewById(R.id.user_edit_weight);
        etHeight = view.findViewById(R.id.user_edit_height);
        rgGender = view.findViewById(R.id.user_gender);

        patientId = DB.patients().getActive(getContext()).getId();
        mPatient = DB.patients().getActive(getContext());

        assetManager = getResources().getAssets();

        genderPicker();


        return view;
    }

    private boolean isValid(String strAge, String strWeight, String strHeight) {
        if (strWeight == null || strHeight == null || gender == null) {
            Toast.makeText(getContext(), "Please enter all numbers" , Toast.LENGTH_SHORT).show();
            return false;
        } else if (Double.parseDouble(strAge) <= 0 || Double.parseDouble(strHeight) <= 0 || Double.parseDouble(strWeight) <= 0) {
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
        String strAge = etAge.getText().toString().trim();
        String strHeight = etHeight.getText().toString().trim();
        String strWeight = etWeight.getText().toString().trim();
        if (gender == null) {
            Toast.makeText(getContext(), "Please enter all numbers" , Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isValid(strAge, strHeight, strWeight)) {
            return;
        }
        int age = Integer.parseInt(strAge);
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

        mPatient.setAge(age);
        mPatient.setHeight(height);
        mPatient.setWeight(weight);
        mPatient.setGender(gender);
        mPatient.setBmi(Double.toString(BMI));
        mPatient.setCondition(condition);
        mPatient.setRecomIntake(intake);

        DB.patients().saveAndFireEvent(mPatient);
        mUserEditCallback.onUserCreated(mPatient);
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
        if (activity instanceof HomeFragment.OnUserEditListener) {
            mUserEditCallback = (HomeFragment.OnUserEditListener) activity;
        }
//        if (activity instanceof ScheduleCreationActivity) {
//            this.showConfirmButton = false;
//        }
    }

    public interface OnUserEditListener {
//        void onUserEdited(Patient p);

        void onUserCreated(Patient p);
    }

}