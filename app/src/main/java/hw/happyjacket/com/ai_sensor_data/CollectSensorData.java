package hw.happyjacket.com.ai_sensor_data;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CollectSensorData extends AppCompatActivity {
    final int MAX_Progress = 50, Progress_interval = 100;
    int current_progress = 0;

    private ProgressDialog collecting;
    private Handler update_progress_dialog;
    private int selectedAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_sensor_data);

        // get the selected action from the Main Activity
        Intent intent = getIntent();
        selectedAction = intent.getIntExtra("selectedAction", 0);

        // create the MySensorManager object to collect sensor data
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        MySensorManager ms = new MySensorManager(sensorManager);

        Button redo_button = (Button) findViewById(R.id.redo_button);
        Button finish_button = (Button) findViewById(R.id.finish_button);
        redo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CollectSensorData.this, "redo", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CollectSensorData.this, MainActivity.class);
                startActivity(intent);
            }
        });
        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CollectSensorData.this, "finish", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CollectSensorData.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // create the ProgessDialog to display the progress collecting sensor data
        collecting = new ProgressDialog(CollectSensorData.this);
        collecting.setTitle("AI sensor data");
        collecting.setMessage("数据数据-ing...");
        collecting.setCancelable(true);
        collecting.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        collecting.setButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
        collecting.setMax(MAX_Progress);
        collecting.show();

        // create the Handler to timer the collecting progress
        update_progress_dialog = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (current_progress < MAX_Progress) {
                    collecting.setProgress(current_progress);
                    update_progress_dialog.postDelayed(update_thread, Progress_interval);
                } else {
                    collecting.dismiss();
                    Toast.makeText(CollectSensorData.this, "done", Toast.LENGTH_SHORT).show();
                }
            }
        };
        update_progress_dialog.postDelayed(update_thread, Progress_interval);


    }

    private Runnable update_thread = new Runnable() {
        @Override
        public void run() {
            current_progress += 1;
            update_progress_dialog.sendEmptyMessage(1);
            update_progress_dialog.removeCallbacks(update_thread);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_collect_sensor_data, menu);
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
