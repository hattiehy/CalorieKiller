package es.usc.citius.servando.calendula.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.graphics.ColorUtils;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.usc.citius.servando.calendula.CalendulaApp;
import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.adapters.ItemClickAdapter;
import es.usc.citius.servando.calendula.database.DB;
import es.usc.citius.servando.calendula.entity.ClickEntity;
import es.usc.citius.servando.calendula.events.PersistenceEvents;
import es.usc.citius.servando.calendula.food.FoodRecognitionException;
import es.usc.citius.servando.calendula.food.FoodRecognitionTask;
import es.usc.citius.servando.calendula.food.FoodServiceCallback;
import es.usc.citius.servando.calendula.food.FoodTask;
import es.usc.citius.servando.calendula.foodrecognizerexample.ImageUtil;
import es.usc.citius.servando.calendula.foodrecognizerexample.JSONUtil;
import es.usc.citius.servando.calendula.fragments.DailyIntakeFragment;
import es.usc.citius.servando.calendula.persistence.DailyIntake;
import es.usc.citius.servando.calendula.persistence.Patient;

public class SelectPicActivity extends AppCompatActivity {

    Button btSelectPic, btDone;
    ImageView ivPic;
    List<Map<String,Object>> mFoodData;
    private RecyclerView mRecyclerView;
    private ItemClickAdapter adapter;
    private double quantityNum = 0;
    private double servingQuantity = 0;
    private int itemPosition = 0;
    private int kjofAllFoods=0;
    private double carbsAll = 0;
    private double fatAll = 0;
    private double proteinAll = 0;
    private Patient user;

    private static final int PHOTO_PHOTOALBUM = 0;
    private static String MY_TOKEN = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pic);
        btSelectPic = findViewById(R.id.bt_select_pic);
        btDone = findViewById(R.id.bt_confirm);
        ivPic = findViewById(R.id.pic);
        mRecyclerView = findViewById(R.id.list);
        MY_TOKEN = getString(R.string.caloriemama_token);

        user = DB.patients().getActive(this);

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
        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CalendulaApp.eventBus().post(new PersistenceEvents.IntakeAddedEvent(kjofAllFoods));
                saveDailyIntake();
                Toast.makeText(SelectPicActivity.this, "Intake has been saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Set color of buttons to be a lightened version of the user color
        btSelectPic.setBackgroundColor(ColorUtils.blendARGB(user.getColor(), Color.WHITE, 0.2f));
        btDone.setBackgroundColor(ColorUtils.blendARGB(user.getColor(), Color.WHITE, 0.2f));
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
                String[] regs = raw.split(", ");
                String strQuantity = regs[regs.length-1];
                servingQuantity = Double.parseDouble(strQuantity.split(" ")[0]);
                popupLayout.dismiss();
                enterQuantity();
            }
        });

    }

    public boolean isSameDay(DailyIntake dailyIntake){
        Date curDate = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String date = formatter.format(curDate);
        if (dailyIntake == null) {
            return false;
        } else
            return dailyIntake.getDate().equals(date);
    }

    public void newRecord(DailyIntake dailyIntake){
        Date curDate = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String date = formatter.format(curDate);

        dailyIntake.setIntake(kjofAllFoods);
        dailyIntake.setCarbs(carbsAll);
        dailyIntake.setFat(fatAll);
        dailyIntake.setProtein(proteinAll);
        dailyIntake.setDate(date);
        dailyIntake.setPatient(user);

        DB.dailyIntake().saveAndFireEvent(dailyIntake);
    }

    public void saveDailyIntake() {
        List<DailyIntake> dailyIntakes = DB.dailyIntake().findAll(user);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dailyIntakes.sort(Comparator.comparing(dailyIntake -> LocalDate.parse(dailyIntake.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")), Comparator.naturalOrder()));
        }
        if (dailyIntakes.size() == 0) {
            DailyIntake dailyIntake = new DailyIntake();
            newRecord(dailyIntake);
        } else {
            DailyIntake oldRecord = dailyIntakes.get(dailyIntakes.size() - 1);
            if (isSameDay(oldRecord)) {
                int intake = oldRecord.getIntake() + kjofAllFoods;
                double carbs = oldRecord.getCarbs() + carbsAll;
                double fat = oldRecord.getFat() + fatAll;
                double protein = oldRecord.getProtein() + proteinAll;
                oldRecord.setIntake(intake);
                oldRecord.setCarbs(carbs);
                oldRecord.setFat(fat);
                oldRecord.setProtein(protein);

                DB.dailyIntake().saveAndFireEvent(oldRecord);
            } else {
                DailyIntake dailyIntakeNew = new DailyIntake();
                newRecord(dailyIntakeNew);
            }
        }

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
                double qua = servingQuantity * quantityNum;
                Map<String, Object> nutritionMap = calculateCalorie(qua);
                int calorie = (Integer) nutritionMap.get("calorie");
                updateItemInfo(calorie);
                kjofAllFoods += calorie;
                carbsAll += (double)nutritionMap.get("carbs");
                fatAll += (double)nutritionMap.get("fat");
                proteinAll += (double) nutritionMap.get("protein");
            }
        });
        popupLayout.setUseRadius(true);
        popupLayout.show(PopupLayout.POSITION_BOTTOM);
    }


    public void updateItemInfo(int totalKj) {
        ClickEntity item = adapter.getData().get(itemPosition);
        String total = totalKj + " kj";
        item.setTotal(total);
        adapter.notifyItemChanged(itemPosition);
    }

    public Map<String, Object> calculateCalorie(double qua) {
        String calorieString = (String) mFoodData.get(itemPosition).get("calorie");
        String carbsString = (String) mFoodData.get(itemPosition).get("totalCarbs");
        String fatString = (String) mFoodData.get(itemPosition).get("totalFat");
        String proteinString = (String) mFoodData.get(itemPosition).get("protein");
        int kjPerkg = Double.valueOf(Double.valueOf(calorieString) * 4.184).intValue();
        int cal = Double.valueOf(kjPerkg * qua).intValue();
        double carbs = Double.valueOf(carbsString) * qua * 1000;
        double fat = Double.valueOf(fatString) * qua * 1000;
        double protein = Double.valueOf(proteinString) * qua * 1000;
        Map<String, Object> nutritionMap = new HashMap<>();
        nutritionMap.put("calorie", cal);
        nutritionMap.put("carbs", carbs);
        nutritionMap.put("fat", fat);
        nutritionMap.put("protein", protein);
        return nutritionMap;
    }


    public void setmRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        try {
            initAdapter();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                Toast.makeText(SelectPicActivity.this, "onItemClick" + position, Toast.LENGTH_SHORT).show();
                itemPosition = position;
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
            String servingWeight = serving.has("servingWeight") ? ", " + serving.getString("servingWeight") + " kg" : "";
            String serDetail = serving.getString("unit") + servingWeight;
            servingList.add(serDetail);
        }
        return servingList;
    }

    private void initAdapter() throws JSONException{
        if (mFoodData.size() == 0) {
            return;
        }
        List<ClickEntity> data = new ArrayList<>();
        for (Map<String,Object> foodList : mFoodData) {
            if ((boolean) foodList.get("hasServingWeight")) {
                int cal = Double.valueOf((String) foodList.get("calorie")).intValue();
                int kj = Double.valueOf(cal * 4.184 / 10).intValue();
                String kjPer100g = String.valueOf(kj);
                data.add(new ClickEntity(ClickEntity.CLICK_ITEM_VIEW, (String) foodList.get("food_name"), kjPer100g + " kj per 100g"));
            } else {
                int cal = Double.valueOf((String) foodList.get("calorie")).intValue();
                int kj = Double.valueOf(cal * 4.184).intValue();
                String kjPer100g = String.valueOf(kj);
                data.add(new ClickEntity(ClickEntity.CLICK_ITEM_VIEW, (String) foodList.get("food_name"), kjPer100g + " kj per serving"));
            }
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
                        int smallSide = bitmap.getHeight() > bitmap.getWidth() ? bitmap.getWidth() : bitmap.getHeight();
                        float scaleRatio = (float) 544/smallSide;
                        Bitmap scaled = ImageUtil.scaleBitmap(bitmap, scaleRatio);
                        int w = scaled.getWidth();
                        int h = scaled.getHeight();
                        Bitmap cropped = ImageUtil.cropCenterImage(scaled, 544, 544);
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