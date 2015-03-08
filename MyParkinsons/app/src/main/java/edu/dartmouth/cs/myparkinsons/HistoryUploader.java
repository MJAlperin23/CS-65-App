package edu.dartmouth.cs.myparkinsons;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tdc on 2/24/15.
 */
public class HistoryUploader {

    public static final String FIELD_ID = "id_col";
    public static final String FIELD_DATE = "day_of_month";
    public static final String FIELD_MONTH = "month_of_year";
    public static final String FIELD_YEAR = "year_col";
    public static final String FIELD_EXERCISE_TIME = "exercise_time";
    public static final String FIELD_SPEECH_ATTEMPTS = "speech_attempts";
    public static final String FIELD_SPEECH_CORRECT = "speech_correct";

    public static void updateHistory(Context context, List<ExerciseItem> entryList, String regId) {
        try {
            JSONArray jsonArray = listToJsonArray(entryList);
            Map<String, String> map = new HashMap<>();
            map.put("entry_list", jsonArray.toString());
            map.put("regId", regId);
            String endpoint = context.getString(R.string.server_addr) + "post.do";
            String result = ServerUtilities.post(endpoint, map);
            Log.d("HistoryUploader", result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static JSONArray listToJsonArray(List<ExerciseItem> entryList) throws JSONException {

        JSONArray jsonArray = new JSONArray();

        int len = entryList.size();
        for (int i = 0; i < len; i++) {
            ExerciseItem entry = entryList.get(i);

            JSONObject j = new JSONObject();
            j.put(FIELD_ID, entry.getId());
            j.put(FIELD_DATE, entry.getDayOfMonth());
            j.put(FIELD_MONTH, entry.getMonthOfYear());
            j.put(FIELD_YEAR, entry.getYear());
            jsonArray.put(j);

        }

        return jsonArray;
    }

}
