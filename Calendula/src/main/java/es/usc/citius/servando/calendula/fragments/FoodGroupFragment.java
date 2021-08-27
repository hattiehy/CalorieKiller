package es.usc.citius.servando.calendula.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.entity.node.BaseNode;

import java.util.ArrayList;
import java.util.List;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.adapters.node.NodeTreeAdapter;
import es.usc.citius.servando.calendula.entity.node.tree.FirstNode;
import es.usc.citius.servando.calendula.entity.node.tree.SecondNode;
import es.usc.citius.servando.calendula.entity.node.tree.ThirdNode;


public class FoodGroupFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private NodeTreeAdapter adapter = new NodeTreeAdapter();

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
        mRecyclerView = view.findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        adapter.setList(getEntity());

        // 模拟新增node
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                SecondNode seNode = new SecondNode(new ArrayList<BaseNode>(), "Second Node(This is added)");
                SecondNode seNode2 = new SecondNode(new ArrayList<BaseNode>(), "Second Node(This is added)");
                List<SecondNode> nodes = new ArrayList<>();
                nodes.add(seNode);
                nodes.add(seNode2);
                //第一个夫node，位置为子node的3号位置
                adapter.nodeAddData(adapter.getData().get(0), 2, nodes);
//                adapter.nodeSetData(adapter.getData().get(0), 2, seNode2);
//                adapter.nodeReplaceChildData(adapter.getData().get(0), nodes);
//                Tips.show("新插入了两个node", Toast.LENGTH_LONG);
            }
        }, 2000);
        return view;
    }

    private List<BaseNode> getEntity() {
        List<BaseNode> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {

            List<BaseNode> secondNodeList = new ArrayList<>();
            for (int n = 0; n <= 5; n++) {

                List<BaseNode> thirdNodeList = new ArrayList<>();
                for (int t = 0; t <= 3; t++) {
                    ThirdNode node = new ThirdNode("Third Node " + t);
                    thirdNodeList.add(node);
                }

                SecondNode seNode = new SecondNode(thirdNodeList, "Second Node " + n);
                secondNodeList.add(seNode);
            }

            FirstNode entity = new FirstNode(secondNodeList, "First Node " + i);

            // 模拟 默认第0个是展开的
            entity.setExpanded(i == 0);

            list.add(entity);
        }
        return list;
    }
}