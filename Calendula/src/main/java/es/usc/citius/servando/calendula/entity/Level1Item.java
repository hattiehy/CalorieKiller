package es.usc.citius.servando.calendula.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import es.usc.citius.servando.calendula.adapters.ExpandableItemAdapter;

public class Level1Item implements  MultiItemEntity {
    public String title;
    public String subTitle;

    public Level1Item(String title, String subTitle) {
        this.subTitle = subTitle;
        this.title = title;
    }

    @Override
    public int getItemType() {
        return ExpandableItemAdapter.TYPE_LEVEL_1;
    }

//    @Override
//    public int getLevel() {
//        return 1;
//    }

}