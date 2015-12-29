package hw.happyjacket.com.ai_sensor_data;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final private String [] actionLabels = {"Running", "Walking", "Listening Music", "Drinking", "Sitting", "Coding", "A", "B", "C", "D", "E", "F", "G"};

    private int selectedAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectedAction = 0;

        Button start = (Button)findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("CollectSensorData");
                intent.putExtra("selectedAction", selectedAction);
                startActivity(intent);
            }
        });

        ListView action_list = (ListView) findViewById(R.id.action_list);
        action_list.setSelection(selectedAction);

        List<Action> actionObjects = getActionObjects();
        ActionAdapter adapter = new ActionAdapter(MainActivity.this, R.layout.action_item, actionObjects);
        action_list.setAdapter(adapter);
        action_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedAction = position;
            }
        });
    }

    private ArrayList<Action> getActionObjects() {
        ArrayList<Action> lists = new ArrayList<>();
        for (int i = 0; i < actionLabels.length; ++i) {
            lists.add(new Action(actionLabels[i], i));
        }
        return lists;
    }


    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
