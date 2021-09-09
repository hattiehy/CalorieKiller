package es.usc.citius.servando.calendula.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class ClickEntity implements MultiItemEntity {
    public static final int CLICK_ITEM_VIEW = 1;
    public int Type;
    private String title;
    private String subTitle;

    public ClickEntity(int type, String title, String subTitle) {
        Type = type;
        this.title = title;
        this.subTitle = subTitle;
    }

    public static int getClickItemView() {
        return CLICK_ITEM_VIEW;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    @Override
    public int getItemType() {
        return Type;
    }
}