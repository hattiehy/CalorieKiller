package es.usc.citius.servando.calendula.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import devliving.online.securedpreferencestore.Logger;
import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.entity.ClickEntity;

public class ItemClickAdapter extends BaseMultiItemQuickAdapter<ClickEntity, BaseViewHolder> implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    public ItemClickAdapter(List<ClickEntity> data) {
        super(data);
        addItemType(ClickEntity.CLICK_ITEM_VIEW, R.layout.item_click_view);
    }


    @Override
    protected void convert(@NonNull final BaseViewHolder helper, final ClickEntity item) {
        switch (helper.getItemViewType()) {
            case ClickEntity.CLICK_ITEM_VIEW:
                helper.setText(R.id.tv, item.getTitle());
                helper.setText(R.id.tv1, item.getSubTitle());
                if (item.getTotal() != null) {
                    helper.setText(R.id.tv2, item.getTotal());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        //
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        baseQuickAdapter.notifyItemChanged(i);
    }

}
