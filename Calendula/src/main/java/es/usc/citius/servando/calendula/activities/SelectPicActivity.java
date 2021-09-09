package es.usc.citius.servando.calendula.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.codingending.popuplayout.PopupLayout;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.adapters.ItemClickAdapter;
import es.usc.citius.servando.calendula.entity.ClickEntity;
import es.usc.citius.servando.calendula.food.FoodRecognitionException;
import es.usc.citius.servando.calendula.food.FoodRecognitionTask;
import es.usc.citius.servando.calendula.food.FoodServiceCallback;
import es.usc.citius.servando.calendula.food.FoodTask;
import es.usc.citius.servando.calendula.foodrecognizerexample.ImageUtil;
import es.usc.citius.servando.calendula.foodrecognizerexample.JSONUtil;

public class SelectPicActivity extends AppCompatActivity {

    Button btSelectPic;
    ImageView ivPic;
    List<Map<String,Object>> mFoodData;
    private RecyclerView mRecyclerView;
    private ItemClickAdapter adapter;
    private double quantityNum = 0;
    private double servingQuantity = 0;
    private int caloriePerkg = 0;
    private int calorie = 0;

    private static final int PHOTO_PHOTOALBUM = 0;
    private static String MY_TOKEN = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pic);
        btSelectPic = findViewById(R.id.bt_select_pic);
        ivPic = findViewById(R.id.pic);
        mRecyclerView = findViewById(R.id.list);
        MY_TOKEN = getString(R.string.caloriemama_token);

        btSelectPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                //其中External为sdcard下的多媒体文件,Internal为system下的多媒体文件。
                //使用INTERNAL_CONTENT_URI只能显示存储在内部的照片
                intent.setDataAndType(
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
                //返回结果和标识
                startActivityForResult(intent, PHOTO_PHOTOALBUM);
            }
        });

        mFoodData = JSONUtil.getInitalListData();
    }

    public void selectServing(final List<String> list) {
        View parent=View.inflate(SelectPicActivity.this,R.layout.layout_food_servings,null);
        ListView listView=parent.findViewById(R.id.listview_bottom);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,list);

        final PopupLayout popupLayout=PopupLayout.init(SelectPicActivity.this,parent);
        popupLayout.setHeight(350,true);
        popupLayout.setUseRadius(true);
        //popupLayout.setWidth(320,true);//手动设置弹出布局的宽度
        popupLayout.show();//默认从底部弹出
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String raw = list.get(i);
                String strQuantity = raw.split(", ")[1];
                servingQuantity = Double.parseDouble(strQuantity.split(" ")[0]);
                popupLayout.dismiss();
                enterQuantity();
            }
        });

    }



    public void enterQuantity() {
        View parent=View.inflate(SelectPicActivity.this,R.layout.layout_buttom,null);
        final PopupLayout popupLayout=PopupLayout.init(SelectPicActivity.this,parent);
        popupLayout.setHeight(350,true);
        final EditText etQuantity = parent.findViewById(R.id.et_serving_num);
        parent.findViewById(R.id.done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isBlank(etQuantity.getText())){
                    Toast.makeText(SelectPicActivity.this, "Please enter the number of servings", Toast.LENGTH_SHORT).show();
                    return;
                }
                quantityNum = Double.parseDouble(etQuantity.getText().toString());
                popupLayout.dismiss();
                calorie = calculateCalorie();
            }
        });
        popupLayout.setUseRadius(true);
        popupLayout.show(PopupLayout.POSITION_BOTTOM);
    }

    public int calculateCalorie() {
        double qua = servingQuantity * quantityNum;
        int calorie = (int) (qua * caloriePerkg);
        return calorie;
    }

    public void setmRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                Toast.makeText(SelectPicActivity.this, "onItemClick" + position, Toast.LENGTH_SHORT).show();
                String s = (String) mFoodData.get(position).get("calorie");
                caloriePerkg = Integer.parseInt(s);
                try {
                    List<String> servingList = setServingList(position);
                    selectServing(servingList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public List<String> setServingList(int position) throws JSONException {
        JSONArray servingArr = (JSONArray) mFoodData.get(position).get("servingSizes");
        List<String> servingList = new ArrayList<>();
        for (int i = 0 ; i < servingArr.length(); i++) {
            JSONObject serving =servingArr.getJSONObject(i);
            String serDetail = serving.getString("unit") + ", " + serving.getString("servingWeight") + " kg";
            servingList.add(serDetail);
        }
        return servingList;
    }

    private void initAdapter() {
        List<ClickEntity> data = new ArrayList<>();
        for (Map<String,Object> foodList : mFoodData) {
            data.add(new ClickEntity(ClickEntity.CLICK_ITEM_VIEW, (String) foodList.get("food_name"), "Calorie per kilogram: " + (String)foodList.get("calorie")));
        }
        adapter = new ItemClickAdapter(data);
        adapter.openLoadAnimation();
        mRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case PHOTO_PHOTOALBUM:
                    Uri imageUri = data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        Bitmap cropped = ImageUtil.cropCenterImage(bitmap, 544, 544);
                        ivPic.setImageBitmap(cropped);
                        FoodTask foodTask = new FoodTask(MY_TOKEN, cropped);
                        final ProgressDialog progressDialog = ProgressDialog.show(this,"Please wait...", "Recognizing food");
                        progressDialog.setCancelable(true);
                        new FoodRecognitionTask(new FoodServiceCallback<JSONObject>() {

                            @Override
                            public void finishRecognition(JSONObject response, FoodRecognitionException exception) {

                                progressDialog.dismiss();
                                boolean isFood = response.optBoolean("is_food");
                                if (!isFood){
                                    Toast.makeText(getApplicationContext(), "Please select a food picture", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                if (exception != null) {
                                    // handle exception gracefully
                                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                                } else {
                                    JSONUtil.foodJsonToList(response, mFoodData);
                                    setmRecyclerView();
                                }

                            }
                        }).execute(foodTask);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }



}