package hw.happyjacket.com.ai_sensor_data;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CollectSensorData extends AppCompatActivity {
    final int MAX_Progress = 50, Progress_interval = 100;
    final String OUT_FILE_NAME = "sensor_data.txt";

    private  int current_progress = 0;
    private ProgressDialog collecting;
    private Handler update_progress_dialog;
    private int selectedAction;
    private SensorManager manager;
    private float[][] buffer = new float[MAX_Progress][MySensorManager.getDataLength()];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_sensor_data);

        // get the selected action from the Main Activity
        Intent intent = getIntent();
        selectedAction = intent.getIntExtra("selectedAction", 0);

        // create the MySensorManager object to collect sensor data
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        MySensorManager msm = new MySensorManager(sensorManager);

        Button redo_button = (Button) findViewById(R.id.redo_button);
        Button finish_button = (Button) findViewById(R.id.finish_button);
        redo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CollectSensorData.this, "重新收集", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CollectSensorData.this, MainActivity.class);
                startActivity(intent);
            }
        });
        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(dataToString());
                Toast.makeText(CollectSensorData.this, "数据已保存成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CollectSensorData.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // create the ProgessDialog to display the progress collecting sensor data
        collecting = new ProgressDialog(CollectSensorData.this);
        collecting.setTitle("传感器数据");
        collecting.setMessage("收集数据ing...");
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

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        final MySensorManager ms = new MySensorManager(manager);
        // create the Handler to timer the collecting progress
        update_progress_dialog = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (current_progress < MAX_Progress) {
                    ms.getSensorData(buffer, current_progress);
                    current_progress += 1;
                    collecting.setProgress(current_progress);
                    update_progress_dialog.postDelayed(update_thread, Progress_interval);
                } else {
                    collecting.dismiss();
                    Toast.makeText(CollectSensorData.this, "收集完成", Toast.LENGTH_SHORT).show();
                }
            }
        };
        update_progress_dialog.postDelayed(update_thread, Progress_interval);
    }

    private void save(String text) {
        FileWriter fw = null;
        String fileName = "";
        try {
            fileName = getExternalFilesDir(null) + File.separator + OUT_FILE_NAME;
            fw = new FileWriter(fileName, true);
            fw.write(text, 0, text.length());
            fw.close();
            Toast.makeText(CollectSensorData.this, "Good "+fileName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ExternalStorage", "Error writing " + fileName);
            Toast.makeText(CollectSensorData.this, "Failed to open file "+fileName, Toast.LENGTH_SHORT).show();
        }
    }

    private String dataToString() {
        String ans = String.format("%d %b %d\n", selectedAction, getWifiInfo(), getVolumeInfo());
        for (int i = 0; i < buffer.length; ++i) {
            for (int j = 0; j < buffer[i].length; ++j) {
                ans += buffer[i][j] + " ";
            }
            ans += "\n";
        }
        Log.d("haha", ans);
        return ans;
    }

    private boolean getWifiInfo() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        boolean status = false;
        if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
        {
            status = true;
        }
        return status;
    }


    // 获取媒体音量而已，还有系统音量，铃声等
    private int getVolumeInfo() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }


    private Runnable update_thread = new Runnable() {
        @Override
        public void run() {
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
