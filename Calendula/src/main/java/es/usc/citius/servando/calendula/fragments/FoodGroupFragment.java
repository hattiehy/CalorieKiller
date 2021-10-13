package es.usc.citius.servando.calendula.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.adapters.ExpandableItemAdapter;

import es.usc.citius.servando.calendula.database.DB;
import es.usc.citius.servando.calendula.entity.Level0Item;
import es.usc.citius.servando.calendula.entity.Level1Item;
import es.usc.citius.servando.calendula.persistence.HealthData;
import es.usc.citius.servando.calendula.persistence.Patient;
import es.usc.citius.servando.calendula.util.IconUtils;
import es.usc.citius.servando.calendula.util.LogUtil;


public class FoodGroupFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ExpandableItemAdapter adapter;
    private ArrayList<MultiItemEntity> list;
    AssetManager assetManager;
    Patient user;
    HealthData record;
    View emptyView;
    IIcon emptyViewIcon = IconUtils.randomNiceIcon();

    private static final String TAG = "FoodGroupFragment";

    public static final String DAIRY_FILE = "Dairy_Nutri_Info_%s.csv";
    public static final String FRUITS_FILE = "Fruits_Nutri_Info_%s.csv";
    public static final String GRAINS_FILE = "Grains_Nutri_Info_%s.csv";
    public static final String MEAT_FILE = "Lean_Meat_Nutri_Info_%s.csv";
    public static final String VEGE_FILE = "Vegetables_Legumes_Nutri_Info_%s.csv";

    public FoodGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_group, container, false);
        mRecyclerView = view.findViewById(R.id.rv);
        emptyView = view.findViewById(R.id.empty_view_placeholder);
        setupEmptyView();
        user = DB.patients().getActive(getContext());
        getNewestData();
        assetManager = getResources().getAssets();
        notifyDataChange();
        return view;
    }

    private void setupRecyclerView() {
        list = generateData();
        adapter = new ExpandableItemAdapter(list);

        final GridLayoutManager manager = new GridLayoutManager(getContext(), 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemViewType(position) == ExpandableItemAdapter.TYPE_LEVEL_1 ? 1 : manager.getSpanCount();
            }
        });

        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
    }

    private List<List<String>> readCSV(String fileName) {
        List<List<String>> nutriList=new ArrayList<>();
        InputStream inputStream;
        BufferedReader bufferedReader;
        String line = null;
        try {
            inputStream = assetManager.open(fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                if ((line.equals("Age Range,Daily Portions,Energy per Portion,Total Daily Energy")))
                    continue;
                String[] content = line.split(",");
                nutriList.add(Arrays.asList(content));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nutriList;
    }

    public List<String> getFileName() {
        String gender = record.getGender();
        List<String> fileNameList = new ArrayList<>();
        fileNameList.add(String.format(DAIRY_FILE, gender));
        fileNameList.add(String.format(FRUITS_FILE, gender));
        fileNameList.add(String.format(GRAINS_FILE, gender));
        fileNameList.add(String.format(MEAT_FILE, gender));
        fileNameList.add(String.format(VEGE_FILE, gender));

        return fileNameList;
    }

    private List<String> searchNutriByAge(List<List<String>> nutriList){
        int age = record.getAge();
        List<String> nutriInfo = new ArrayList<>();
        for (List<String> list : nutriList) {
            List<String> ageRage = Arrays.asList(list.get(0).split(" - "));
            int minAge = Integer.parseInt(ageRage.get(0));
            int maxAge = Integer.parseInt(ageRage.get(1));
            if ( minAge<= age && age < maxAge) {
                for (int i = 1; i < 4; i++) {
                    nutriInfo.add(list.get(i));
                }
            }
        }
        return nutriInfo;
    }


    private ArrayList<MultiItemEntity> generateData() {
//        int lv0Count = 9;
//        int lv1Count = 3;

        ArrayList<MultiItemEntity> res = new ArrayList<>();
//        for (int i = 0; i < lv0Count; i++) {
//            Level0Item lv0 = new Level0Item("This is " + i + "th item in Level 0", "subtitle of " + i);
//            for (int j = 0; j < lv1Count; j++) {
//                Level1Item lv1 = new Level1Item("Level 1 item: " + j, "(no animation)");
//                lv0.addSubItem(lv1);
//            }
//            res.add(lv0);
//        }

        List<String> diary = new ArrayList<>();
        List<String> fruit = new ArrayList<>();
        List<String> grains = new ArrayList<>();
        List<String> meat = new ArrayList<>();
        List<String> vege = new ArrayList<>();

        List<String> nameList = getFileName();
        List<List<String>> diaryList = readCSV(nameList.get(0));
        diary = searchNutriByAge(diaryList);
        List<List<String>> fruitList = readCSV(nameList.get(1));
        fruit = searchNutriByAge(fruitList);
        List<List<String>> grainsList = readCSV(nameList.get(2));
        grains = searchNutriByAge(grainsList);
        List<List<String>> meatList = readCSV(nameList.get(3));
        meat = searchNutriByAge(meatList);
        List<List<String>> vegeList = readCSV(nameList.get(4));
        vege = searchNutriByAge(vegeList);

        Level0Item lv1 = new Level0Item("Dairy Nutritional Recommendation", "subtitle of ");
        lv1.addSubItem(new Level1Item("Daily Portions\n - \n" + diary.get(0), "(no animation)"));
        lv1.addSubItem(new Level1Item("Energy per Portion\n - \n" + diary.get(1) + " kj", "(no animation)"));
        lv1.addSubItem(new Level1Item("Total Daily Energy\n - \n" + diary.get(2) + " kj", "(no animation)"));
        res.add(lv1);

        Level0Item lv2 = new Level0Item("Fruit Nutritional Recommendation", "subtitle of ");
        lv2.addSubItem(new Level1Item("Daily Portions\n - \n" + fruit.get(0), "(no animation)"));
        lv2.addSubItem(new Level1Item("Energy per Portion\n - \n" + fruit.get(1) + " kj", "(no animation)"));
        lv2.addSubItem(new Level1Item("Total Daily Energy\n - \n" + fruit.get(2) + " kj", "(no animation)"));
        res.add(lv2);

        Level0Item lv3 = new Level0Item("Grains Nutritional Recommendation", "subtitle of ");
        lv3.addSubItem(new Level1Item("Daily Portions\n - \n" + grains.get(0), "(no animation)"));
        lv3.addSubItem(new Level1Item("Energy per Portion\n - \n" + grains.get(1) + " kj", "(no animation)"));
        lv3.addSubItem(new Level1Item("Total Daily Energy\n - \n" + grains.get(2) + " kj", "(no animation)"));
        res.add(lv3);

        Level0Item lv4 = new Level0Item("Lean Meat Nutritional Recommendation", "subtitle of ");
        lv4.addSubItem(new Level1Item("Daily Portions\n - \n" + meat.get(0), "(no animation)"));
        lv4.addSubItem(new Level1Item("Energy per Portion\n - \n" + meat.get(1) + " kj", "(no animation)"));
        lv4.addSubItem(new Level1Item("Total Daily Energy\n - \n" + meat.get(2) + " kj", "(no animation)"));
        res.add(lv4);

        Level0Item lv5 = new Level0Item("Vegetables & Legumes Nutritional Recommendation", "subtitle of ");
        lv5.addSubItem(new Level1Item("Daily Portions\n - \n" + vege.get(0), "(no animation)"));
        lv5.addSubItem(new Level1Item("Energy per Portion\n - \n" + vege.get(1) + " kj", "(no animation)"));
        lv5.addSubItem(new Level1Item("Total Daily Energy\n - \n" + vege.get(2) + " kj", "(no animation)"));
        res.add(lv5);
        return res;
    }

    public void notifyDataChange() {
        getNewestData();
        try {
//            rvAdapter.notifyDataSetChanged();
            // show empty list view if there are no items
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOrHideEmptyView(isEmpty());
                }
            }, 100);
        } catch (Exception e) {
            LogUtil.e(TAG, "Error onPostExecute", e);
        }
    }

    public boolean isEmpty() {
        if (record == null) {
            return true;
        }
        return false;
    }

    public void getNewestData(){
        List<HealthData> healthDataList = DB.healthData().findAllForActivePatient(getContext());
        if (healthDataList.isEmpty()) {
            record = null;
        } else {
            record = healthDataList.get(healthDataList.size() - 1 );
        }
    }

    public void onUserUpdate(Patient patient) {
        user = patient;
        notifyDataChange();
    }

    private void setupEmptyView() {
        int color = HomeProfileMgr.colorForCurrent(getActivity());
        Drawable icon = new IconicsDrawable(getContext())
                .icon(emptyViewIcon)
                .color(color)
                .sizeDp(90)
                .paddingDp(0);
        ((ImageView) emptyView.findViewById(R.id.imageView_ok)).setImageDrawable(icon);
    }

    private void onBackgroundChange(int color) {
        Drawable icon = new IconicsDrawable(getContext())
                .icon(emptyViewIcon)
                .color(color)
                .sizeDp(90)
                .paddingDp(0);
        ((ImageView) emptyView.findViewById(R.id.imageView_ok)).setImageDrawable(icon);
    }

    public void showOrHideEmptyView(boolean show) {
        if (show) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.animate().alpha(1);
        } else {
            emptyView.setVisibility(View.GONE);
            setupRecyclerView();
            emptyView.animate().alpha(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                }
            });

        }
    }
}