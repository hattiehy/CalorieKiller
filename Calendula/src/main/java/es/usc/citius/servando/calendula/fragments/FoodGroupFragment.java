package es.usc.citius.servando.calendula.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.chad.library.adapter.base.entity.MultiItemEntity;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.adapters.ExpandableItemAdapter;

import es.usc.citius.servando.calendula.entity.Level0Item;
import es.usc.citius.servando.calendula.entity.Level1Item;



public class FoodGroupFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ExpandableItemAdapter adapter;
    private ArrayList<MultiItemEntity> list;

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
        adapter.expandAll();

        return view;
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
        Level0Item lv1 = new Level0Item("Diary Nutri Info", "subtitle of ");
        lv1.addSubItem(new Level1Item("Daily Portions: 500-600", "(no animation)"));
        lv1.addSubItem(new Level1Item("Energy per Portion 500-600", "(no animation)"));
        lv1.addSubItem(new Level1Item("Total Daily Energy 500-900", "(no animation)"));
        res.add(lv1);

        Level0Item lv2 = new Level0Item("Fruits Nutri Info", "subtitle of ");
        lv2.addSubItem(new Level1Item("Daily Portions: 0.5-1", "(no animation)"));
        lv2.addSubItem(new Level1Item("Energy per Portion 350", "(no animation)"));
        lv2.addSubItem(new Level1Item("Total Daily Energy 175-350", "(no animation)"));
        res.add(lv2);

        Level0Item lv3 = new Level0Item("Grains Nutri Info", "subtitle of ");
        lv3.addSubItem(new Level1Item("Daily Portions: 4-5", "(no animation)"));
        lv3.addSubItem(new Level1Item("Energy per Portion 500", "(no animation)"));
        lv3.addSubItem(new Level1Item("Total Daily Energy 2000", "(no animation)"));
        res.add(lv3);

        Level0Item lv4 = new Level0Item("Lean Meat Nutri Info", "subtitle of ");
        lv4.addSubItem(new Level1Item("Daily Portions: 1-1.5", "(no animation)"));
        lv4.addSubItem(new Level1Item("Energy per Portion 600", "(no animation)"));
        lv4.addSubItem(new Level1Item("Total Daily Energy 600-900", "(no animation)"));
        res.add(lv4);

        Level0Item lv5 = new Level0Item("Vegetables Legumes Nutri Info", "subtitle of ");
        lv5.addSubItem(new Level1Item("Daily Portions: 2-2.5", "(no animation)"));
        lv5.addSubItem(new Level1Item("Energy per Portion 350", "(no animation)"));
        lv5.addSubItem(new Level1Item("Total Daily Energy 700-900", "(no animation)"));
        res.add(lv5);
        return res;
    }
}