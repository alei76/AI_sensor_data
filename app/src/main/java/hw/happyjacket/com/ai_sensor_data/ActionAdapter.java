package hw.happyjacket.com.ai_sensor_data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jacket on 2015/12/29.
 */
public class ActionAdapter extends ArrayAdapter<Action> {
    private int resourceId;

    public ActionAdapter(Context context, int ItemResourceId, List<Action> objects) {
        super(context, ItemResourceId, objects);
        resourceId = ItemResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Action action = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView actionLabel = (TextView) view.findViewById(R.id.action_label);
        actionLabel.setText(action.getLabel() + String.format("(%d)", action.getNum()));
        /*Button number = (Button) view.findViewById(R.id.num_of_action);
        number.setText("" + action.getNum());*/
        return view;
    }
}
