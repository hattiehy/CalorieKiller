package es.usc.citius.servando.calendula.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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

import java.util.Objects;

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

        genderPicker();

        return view;
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
        Integer age = Integer.parseInt(etAge.getText().toString().trim());
        double height = Double.parseDouble(etHeight.getText().toString().trim());
        double weight = Double.parseDouble(etWeight.getText().toString().trim());

        mPatient.setAge(age);
        mPatient.setHeight(height);
        mPatient.setWeight(weight);
        mPatient.setGender(gender);
        DB.patients().saveAndFireEvent(mPatient);
        mUserEditCallback.onUserCreated(mPatient);
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