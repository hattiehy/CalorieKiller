package es.usc.citius.servando.calendula.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.model.Dose;
import es.usc.citius.servando.calendula.model.Routine;
import es.usc.citius.servando.calendula.model.ScheduleItem;
import es.usc.citius.servando.calendula.model.ScheduleItemComparator;
import es.usc.citius.servando.calendula.store.RoutineStore;
import es.usc.citius.servando.calendula.util.GsonUtil;
import es.usc.citius.servando.calendula.util.ScheduleCreationHelper;


/**
 * Created by joseangel.pineiro on 12/11/13.
 */
public class ScheduleTimetableFragment extends Fragment {

    public static final String TAG = ScheduleTimetableFragment.class.getName();

    LinearLayout timetableContainer;
    Integer doses[] = new Integer[]{1, 2, 3, 4, 5, 6};
    int timesPerDay = 1;

    Spinner scheduleSpinner;

    ScheduleItemComparator scheduleItemComparator = new ScheduleItemComparator();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule_timetable, container, false);
        timetableContainer = (LinearLayout) rootView.findViewById(R.id.schedule_timetable_container);
        setupScheduleSpinner(rootView);
        setupDaySelectionListeners(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        timesPerDay = ScheduleCreationHelper.instance().getTimesPerDay();
        scheduleSpinner.setSelection(ScheduleCreationHelper.instance().getSelectedScheduleIdx());
        checkSelectedDays(view, ScheduleCreationHelper.instance().getSelectedDays());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("RESUME", "Idx: " + ScheduleCreationHelper.instance().getSelectedScheduleIdx());
        Log.d("RESUME", "Days: " + Arrays.toString(ScheduleCreationHelper.instance().getSelectedDays()));
        Log.d("RESUME", "Schedule: " + ScheduleCreationHelper.instance().getTimesPerDay());
        //Log.d("RESUME", "Times: " + Arrays.toString(ScheduleCreationHelper.instance().getSelectedRoutines().toArray()));
    }

    private void setupScheduleSpinner(View rootView) {
        scheduleSpinner = (Spinner) rootView.findViewById(R.id.schedules_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.schedules_array, R.layout.spinner_text_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        scheduleSpinner.setAdapter(adapter);

        scheduleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = (String) adapterView.getItemAtPosition(i);
                ScheduleCreationHelper.instance().setSelectedScheduleIdx(i);
                Log.d(getTag(), "Selected: " + selected);
                onScheduleSelected(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });
    }

    void onScheduleSelected(String selection) {

        String schedules[] = getResources().getStringArray(R.array.schedules_array);

        // obtain times per day from selected schedule
        for (int i = 0; i < schedules.length; i++) {
            if (schedules[i].equalsIgnoreCase(selection)) {
                timesPerDay = i + 1;
                ScheduleCreationHelper.instance().setTimesPerDay(timesPerDay);
                break;
            }
        }

        List<Routine> routines = RoutineStore.instance().asList();
        addTimetableEntries(timesPerDay, routines);
    }


    void setupDaySelectionListeners(View rootView) {

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView text = ((TextView) view);


                if (text.getTag() == null || ((Boolean) text.getTag()) == true) {
                    text.setTag(false);
                    ((TextView) view).setTextAppearance(getActivity(), R.style.schedule_day_unselected);
                } else {
                    text.setTag(true);
                    ((TextView) view).setTextAppearance(getActivity(), R.style.schedule_day_selected);
                }

                switch (text.getId()) {
                    case R.id.day_mo:
                        ScheduleCreationHelper.instance().toggleSelectedDay(0);
                        break;
                    case R.id.day_tu:
                        ScheduleCreationHelper.instance().toggleSelectedDay(1);
                        break;
                    case R.id.day_we:
                        ScheduleCreationHelper.instance().toggleSelectedDay(2);
                        break;
                    case R.id.day_th:
                        ScheduleCreationHelper.instance().toggleSelectedDay(3);
                        break;
                    case R.id.day_fr:
                        ScheduleCreationHelper.instance().toggleSelectedDay(4);
                        break;
                    case R.id.day_sa:
                        ScheduleCreationHelper.instance().toggleSelectedDay(5);
                        break;
                    case R.id.day_su:
                        ScheduleCreationHelper.instance().toggleSelectedDay(6);
                        break;
                    default:
                        break;
                }
            }
        };

        rootView.findViewById(R.id.day_mo).setOnClickListener(listener);
        rootView.findViewById(R.id.day_tu).setOnClickListener(listener);
        rootView.findViewById(R.id.day_we).setOnClickListener(listener);
        rootView.findViewById(R.id.day_th).setOnClickListener(listener);
        rootView.findViewById(R.id.day_fr).setOnClickListener(listener);
        rootView.findViewById(R.id.day_sa).setOnClickListener(listener);
        rootView.findViewById(R.id.day_su).setOnClickListener(listener);


    }


    void addTimetableEntries(int timesPerDay, List<Routine> routines) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        String[] routineNames = getUpdatedRoutineNames();
        timetableContainer.removeAllViews();

        Collections.sort(ScheduleCreationHelper.instance().getScheduleItems(), scheduleItemComparator);

        for(ScheduleItem i : ScheduleCreationHelper.instance().getScheduleItems()){
            Routine r = RoutineStore.instance().get(i.routineId());
            Log.d("SORT", r!=null?r.getName():"null");
        }

        List<ScheduleItem> scheduleItems = new ArrayList<ScheduleItem>();

        boolean enableDelete = timesPerDay > 1;

        for (int i = 0; i < timesPerDay; i++) {
            // try to get previous routine from state holder
            ScheduleItem  s = (i < ScheduleCreationHelper.instance().getScheduleItems().size()) ?
                    ScheduleCreationHelper.instance().getScheduleItems().get(i) : null;

            // if null, get it from the store if possible

            Log.d("ROUTINES", "Routines: " + routines.size() +", i:" + i);

            if (s == null) {
                s = new ScheduleItem((i < routines.size()) ? routines.get(i).id() : null, new Dose(1));
            }

            Log.d("ROUTINES", s.toString());

            if(s !=null){
                scheduleItems.add(s);
            }

            View view = buildTimetableEntry(s, routineNames, enableDelete);
            timetableContainer.addView(view, params);
        }

        ScheduleCreationHelper.instance().setScheduleItems(scheduleItems);
        Log.d(TAG, "SetScheduleItems: " + GsonUtil.get().toJson(scheduleItems));

    }


    String[] getUpdatedRoutineNames() {

        List<Routine> routines = RoutineStore.instance().asList();

        int j = 0;
        String[] routineNames = new String[routines.size() + 1];
        for (Routine r : routines) {
            routineNames[j++] = r.getName();
        }

        routineNames[routineNames.length - 1] = getString(R.string.create_new_routine);

        return routineNames;
    }


    View buildTimetableEntry(ScheduleItem r, String[] routineNames, boolean enableDelete) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View entry = inflater.inflate(R.layout.schedule_timetable_entry, null);
        updateEntryTime(RoutineStore.instance().get(r.routineId()), entry);
        setupScheduleEntrySpinners(entry, r, routineNames);

        if(enableDelete) {
            entry.findViewById(R.id.entry_remove).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ScheduleCreationHelper.instance().getScheduleItems().remove(timetableContainer.indexOfChild(entry));
                    scheduleSpinner.setSelection(timesPerDay - 2);
                }
            });
        }else{
            entry.findViewById(R.id.entry_remove).setVisibility(View.INVISIBLE);
        }
        return entry;
    }

    void updateRoutineSelectionAdapter(final View entryView, Spinner routineSpinner, String[] routineNames) {
        ArrayAdapter<String> routineAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, routineNames);
        routineAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        routineSpinner.setAdapter(routineAdapter);
    }


    private void setupScheduleEntrySpinners(final View entryView, ScheduleItem scheduleItem, String[] routineNames) {

        final Spinner routineSpinner = (Spinner) entryView.findViewById(R.id.entry_routine_spinner);
        final Spinner doseSpinner = (Spinner) entryView.findViewById(R.id.entry_dose_spinner);

        doseSpinner.setTag(scheduleItem);
        routineSpinner.setTag(scheduleItem);

        // set up the routine selection adapter
        updateRoutineSelectionAdapter(entryView, routineSpinner, routineNames);

        if (scheduleItem != null && scheduleItem.routineId()!=null) {
            Log.d("ROUTINES", "BeforeNull: " + scheduleItem.toString());
            String routineId = scheduleItem.routineId();
            String routineName = RoutineStore.instance().get(routineId).getName();
            int index = Arrays.asList(routineNames).indexOf(routineName);
            routineSpinner.setSelection(index);
        } else {
            routineSpinner.setSelection(routineNames.length - 1);
        }

        // set up the dose selection adapter
        ArrayAdapter<Integer> doseAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, doses);
        doseAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        doseSpinner.setAdapter(doseAdapter);
        // select 1 pill by default
        doseSpinner.setSelection(scheduleItem.dose().ammount() - 1); // dose "1" is located at the index "0", and so on

        // setup listeners
        routineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = (String) adapterView.getItemAtPosition(i);
                Routine r = RoutineStore.instance().getRoutineByName(selected);
                ScheduleItem item = ((ScheduleItem)doseSpinner.getTag());

                if (r != null) {
                    updateEntryTime(r, entryView);
                    item.setRoutine(r.id());
                } else {
                    updateEntryTime(null, entryView);
                    showAddNewRoutineDialog(entryView);
                }

                int idx = timetableContainer.indexOfChild(entryView);
                if (ScheduleCreationHelper.instance().getScheduleItems().size() > idx) {
                    ScheduleCreationHelper.instance().getScheduleItems().remove(idx);
                }
                // r can be null, yes
                ScheduleCreationHelper.instance().getScheduleItems().add(idx, new ScheduleItem(r!=null?r.id():null,item.dose()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        routineSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    if (((String) routineSpinner.getSelectedItem()).equalsIgnoreCase(getString(R.string.create_new_routine))) {
                        showAddNewRoutineDialog(entryView);
                        return true;
                    }
                }
                return false;
            }
        });


        doseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Integer selected = (Integer) adapterView.getItemAtPosition(i);
                ScheduleItem item = ((ScheduleItem)doseSpinner.getTag());
                item.dose().setAmmount(selected);
                Log.d(TAG, "Set dose " + selected + " for scheduleItem " + item.id());
                Log.d(TAG, GsonUtil.get().toJson(ScheduleCreationHelper.instance().getScheduleItems()));
                //Log.d(getTag(), "Selected: " + selected + " for routine " + (item.routineId() != null ? RoutineStore.instance().get(item.routineId()).getName() : "[new]"));
                //Log.d(getTag(), "Selected: " + selected + " for routine " + (item.routineId() != null ? RoutineStore.instance().get(item.routineId()).getName() : "[new]"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    void updateEntryTime(Routine r, View entry) {
        String hourText;
        String minuteText;
        if (r != null) {
            hourText = (r.getTime().getHourOfDay() < 10 ? "0" + r.getTime().getHourOfDay() : r.getTime().getHourOfDay()) + ":";
            minuteText = (r.getTime().getMinuteOfHour() < 10 ? "0" + r.getTime().getMinuteOfHour() : r.getTime().getMinuteOfHour()) + "";
        } else {
            hourText = "--:";
            minuteText = "--";
        }

        ((TextView) entry.findViewById(R.id.hour_text)).setText(hourText);
        ((TextView) entry.findViewById(R.id.minute_text)).setText(minuteText);
    }


    void showAddNewRoutineDialog(final View entryView) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        final RoutineCreateOrEditFragment addRoutineFragment = new RoutineCreateOrEditFragment();
        addRoutineFragment.setOnRoutineEditListener(new RoutineCreateOrEditFragment.OnRoutineEditListener() {
            @Override
            public void onRoutineEdited(Routine r) {
                // do nothing
            }

            @Override
            public void onRoutineCreated(final Routine r) {
                Spinner rSpinner = (Spinner) entryView.findViewById(R.id.entry_routine_spinner);
                String names[] = getUpdatedRoutineNames();
                updateRoutineSelectionAdapter(entryView, rSpinner, names);

                Log.d(getTag(), "Routine name: " + r.getName());
                Log.d(getTag(), "Routine time: " + r.getTimeAsString());
                Log.d(getTag(), "Names: " + Arrays.toString(names));

                int selection = Arrays.asList(names).indexOf(r.getName());
                rSpinner.setSelection(selection);

                updateEntryTime(r, entryView);
                addRoutineFragment.dismiss();
            }
        });

        addRoutineFragment.show(fm, "fragment_edit_name");
    }

    void checkSelectedDays(View rootView, boolean[] days) {

        ((TextView) rootView.findViewById(R.id.day_mo)).setTextAppearance(getActivity(),
                days[0] ? R.style.schedule_day_selected : R.style.schedule_day_unselected);
        ((TextView) rootView.findViewById(R.id.day_tu)).setTextAppearance(getActivity(),
                days[1] ? R.style.schedule_day_selected : R.style.schedule_day_unselected);
        ((TextView) rootView.findViewById(R.id.day_we)).setTextAppearance(getActivity(),
                days[2] ? R.style.schedule_day_selected : R.style.schedule_day_unselected);
        ((TextView) rootView.findViewById(R.id.day_th)).setTextAppearance(getActivity(),
                days[3] ? R.style.schedule_day_selected : R.style.schedule_day_unselected);
        ((TextView) rootView.findViewById(R.id.day_fr)).setTextAppearance(getActivity(),
                days[4] ? R.style.schedule_day_selected : R.style.schedule_day_unselected);
        ((TextView) rootView.findViewById(R.id.day_sa)).setTextAppearance(getActivity(),
                days[5] ? R.style.schedule_day_selected : R.style.schedule_day_unselected);
        ((TextView) rootView.findViewById(R.id.day_su)).setTextAppearance(getActivity(),
                days[6] ? R.style.schedule_day_selected : R.style.schedule_day_unselected);

    }

}