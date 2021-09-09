package es.usc.citius.servando.calendula.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
//        addItemType(ClickEntity.CLICK_ITEM_CHILD_VIEW, R.layout.item_click_childview);
//        addItemType(ClickEntity.LONG_CLICK_ITEM_VIEW, R.layout.item_long_click_view);
//        addItemType(ClickEntity.LONG_CLICK_ITEM_CHILD_VIEW, R.layout.item_long_click_childview);
//        addItemType(ClickEntity.NEST_CLICK_ITEM_CHILD_VIEW, R.layout.item_nest_click);

    }


    @Override
    protected void convert(@NonNull final BaseViewHolder helper, final ClickEntity item) {
        switch (helper.getItemViewType()) {
            case ClickEntity.CLICK_ITEM_VIEW:
                helper.addOnClickListener(R.id.btn);
                helper.setText(R.id.tv, item.getTitle());
                helper.setText(R.id.tv1, item.getSubTitle());
                break;
//            case ClickEntity.CLICK_ITEM_CHILD_VIEW:
//                helper.addOnClickListener(R.id.iv_num_reduce).addOnClickListener(R.id.iv_num_add)
//                        .addOnLongClickListener(R.id.iv_num_reduce).addOnLongClickListener(R.id.iv_num_add);
//                // set img data
//                break;
//            case ClickEntity.LONG_CLICK_ITEM_VIEW:
//                helper.addOnLongClickListener(R.id.btn);
//                break;
//            case ClickEntity.LONG_CLICK_ITEM_CHILD_VIEW:
//                helper.addOnLongClickListener(R.id.iv_num_reduce).addOnLongClickListener(R.id.iv_num_add)
//                        .addOnClickListener(R.id.iv_num_reduce).addOnClickListener(R.id.iv_num_add);
//                break;
//            case ClickEntity.NEST_CLICK_ITEM_CHILD_VIEW:
//                // u can set nestview id
//                helper.setNestView(R.id.item_click);
//                final RecyclerView recyclerView = helper.getView(R.id.nest_list);
//                recyclerView.setLayoutManager(new LinearLayoutManager(helper.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
//                recyclerView.setHasFixedSize(true);
//
//                nestAdapter = new NestAdapter();
//                nestAdapter.setOnItemClickListener(this);
//                nestAdapter.setOnItemChildClickListener(this);
//                recyclerView.setAdapter(nestAdapter);
//                break;
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
        //
    }

//    @Override
//    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//        Toast.makeText(Utils.getContext(), "childView click", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//        Logger.d("嵌套RecycleView item 收到: " + "点击了第 " + position + " 一次");
//        Toast.makeText(Utils.getContext(), "嵌套RecycleView item 收到: " + "点击了第 " + position + " 一次", Toast.LENGTH_SHORT).show();
//    }
}
