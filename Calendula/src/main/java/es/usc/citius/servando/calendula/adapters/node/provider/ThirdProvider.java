package es.usc.citius.servando.calendula.adapters.node.provider;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import es.usc.citius.servando.calendula.R;

import org.jetbrains.annotations.NotNull;

import es.usc.citius.servando.calendula.entity.node.tree.ThirdNode;

public class ThirdProvider extends BaseNodeProvider {

    @Override
    public int getItemViewType() {
        return 3;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_node_third;
    }

    @Override
    public void convert(@NotNull BaseViewHolder helper, @NotNull BaseNode data) {
        ThirdNode entity = (ThirdNode) data;
        helper.setText(R.id.title, entity.getTitle());
    }
}
