package es.usc.citius.servando.calendula.adapters.node;


import com.chad.library.adapter.base.BaseNodeAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import es.usc.citius.servando.calendula.adapters.node.provider.FirstProvider;
import es.usc.citius.servando.calendula.adapters.node.provider.SecondProvider;
import es.usc.citius.servando.calendula.adapters.node.provider.ThirdProvider;
import es.usc.citius.servando.calendula.entity.node.tree.FirstNode;
import es.usc.citius.servando.calendula.entity.node.tree.SecondNode;
import es.usc.citius.servando.calendula.entity.node.tree.ThirdNode;

public class NodeTreeAdapter extends BaseNodeAdapter {

    public NodeTreeAdapter() {
        super();
        addNodeProvider(new FirstProvider());
        addNodeProvider(new SecondProvider());
        addNodeProvider(new ThirdProvider());
    }

    @Override
    protected int getItemType(@NotNull List<? extends BaseNode> data, int position) {
        BaseNode node = data.get(position);
        if (node instanceof FirstNode) {
            return 1;
        } else if (node instanceof SecondNode) {
            return 2;
        } else if (node instanceof ThirdNode) {
            return 3;
        }
        return -1;
    }

    public static final int EXPAND_COLLAPSE_PAYLOAD = 110;
}