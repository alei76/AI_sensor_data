package hw.happyjacket.com.ai_sensor_data;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //final private String [] actionLabels = {"Running", "Walking", "Listening Music", "Drinking", "Sitting", "Coding", "A", "B", "C", "D", "E", "F", "G"};
    final private String [] actionLabels = {"跑步", "走路", "听音乐", "喝咖啡等", "休息中", "打代码"};

    private int selectedAction = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("CollectSensorData");
                intent.putExtra("selectedAction", selectedAction);
                startActivity(intent);
            }
        });

        Button predict = (Button) findViewById(R.id.predict);
        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("PredictAction");
                intent.putExtra("selectedAction", selectedAction);
                intent.putExtra("doing", actionLabels[selectedAction]);
                startActivity(intent);
            }
        });

        final ListView action_list = (ListView) findViewById(R.id.action_list);
        action_list.setSelection(selectedAction);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, actionLabels);
        action_list.setAdapter(adapter);
        action_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedAction = position;
                Toast.makeText(MainActivity.this, "选择动作：" + actionLabels[selectedAction], Toast.LENGTH_SHORT).show();
            }
        });
    }


    protected void onDestroy() {
        super.onDestroy();
    }
}
