package hw.happyjacket.com.ai_sensor_data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jacket on 2015/12/29.
 */
public class Action {
    private String label;
    private int id;
    private Context m_context;

    public Action(Context context, String label, int id) {
        this.m_context = context;
        this.label = label;
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public int getId() {
        return id;
    }

    public void addNum() {
        SharedPreferences.Editor editor = m_context.getSharedPreferences(SETTINGS.Action_num_shareP_file, Context.MODE_PRIVATE).edit();
        int now = getNum();
        editor.putInt(SETTINGS.SharePref_pre + id, now + 1);
        editor.commit();
    }

    public int getNum() {
        SharedPreferences preferences = m_context.getSharedPreferences(SETTINGS.Action_num_shareP_file, Context.MODE_PRIVATE);
        return preferences.getInt(SETTINGS.SharePref_pre + id, 0);
    }
}
