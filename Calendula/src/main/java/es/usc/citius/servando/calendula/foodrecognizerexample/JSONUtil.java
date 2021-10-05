package es.usc.citius.servando.calendula.foodrecognizerexample;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by azumio.azumio
 */

public class JSONUtil {


    public static List<Map<String, Object>> getInitalListData() {
        List<Map<String, Object>> list = new ArrayList<>();
        return list;
    }

    public static void foodJsonToList(JSONObject response, List<Map<String, Object>> list) {

        list.clear();

        if (response != null) {
            JSONArray results = response.optJSONArray("results");
            for (int i=0; i<results.length(); i++) {
                JSONObject result = results.optJSONObject(i);
                JSONArray items = result.optJSONArray("items");
                if (items.length() > 0){
                    try {
                        String name = result.getString("group");
                        Object item = items.get(0);
                        list.add(createItem(name, item));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static Map<String, Object> createItem(String groupName, Object calories) throws JSONException {
        JSONObject cal = (JSONObject) calories;
        JSONObject nutrition = cal.optJSONObject("nutrition");
        JSONArray servingSizes = cal.optJSONArray("servingSizes");
        JSONObject serving = servingSizes.getJSONObject(0);
        Map<String, Object>  item =  new HashMap<>();
        item.put("group_name", groupName);
        item.put("calorie", nutrition.getString("calories"));
        item.put("totalCarbs", nutrition.getString("totalCarbs"));
        item.put("totalFat", nutrition.getString("totalFat"));
        item.put("protein", nutrition.getString("protein"));
        item.put("food_name", cal.getString("name"));
        item.put("servingSizes", servingSizes);
        item.put("hasServingWeight", serving.has("servingWeight"));
        return item;
    }

}
