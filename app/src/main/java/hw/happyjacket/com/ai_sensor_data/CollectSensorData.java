package hw.happyjacket.com.ai_sensor_data;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CollectSensorData extends AppCompatActivity {
    private int selectedAction;
    private SensorDataProxy proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_sensor_data);

        // 获取从MainActivity传过来的选中的动作：selectedAction
        Intent intent = getIntent();
        selectedAction = intent.getIntExtra("selectedAction", 0);

        // 新建传感器数据收集代理对象来收集数据
        proxy = new SensorDataProxy(CollectSensorData.this, selectedAction);
        proxy.Collecting();

        // 设置重新收集按钮：不记录这一次收集的数据
        Button redo_button = (Button) findViewById(R.id.redo_button);
        redo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CollectSensorData.this, "重新收集", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CollectSensorData.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 设置保存数据按钮
        Button finish_button = (Button) findViewById(R.id.finish_button);
        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proxy.save(proxy.train_raw_file_name, proxy.dataToString(false));
                proxy.addOne();
                Toast.makeText(CollectSensorData.this, "数据已保存成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CollectSensorData.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
