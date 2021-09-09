package es.usc.citius.servando.calendula.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import com.chad.library.adapter.base.BaseQuickAdapter;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    public void setmRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(SelectPicActivity.this, "onItemClick" + position, Toast.LENGTH_SHORT).show();
            }
        });
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