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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class PredictAction extends AppCompatActivity {
    final int MAX_Progress = CollectSensorData.MAX_Progress, Progress_interval = CollectSensorData.Progress_interval;
    private  int current_progress;
    private ProgressDialog collecting;
    private int selectedAction;
    private SensorManager manager;
    private float[][] buffer = new float[MAX_Progress][MySensorManager.getDataLength()];
    private boolean cancelled = false;
    private Handler update_progress_dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict_action);

        Intent intent = getIntent();
        selectedAction = intent.getIntExtra("selectedAction", 0);
        final String doingWhat = intent.getStringExtra("doing");

        Button start = (Button) findViewById(R.id.startPredict);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_progress = 0;
                cancelled = false;
                update_progress_dialog.postDelayed(update_thread, Progress_interval);
                collecting.show();
            }
        });

        Button back = (Button) findViewById(R.id.backToMain);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PredictAction.this, MainActivity.class);
                startActivity(intent);
            }
        });


        // create the ProgessDialog to display the progress collecting sensor data
        collecting = new ProgressDialog(PredictAction.this);
        collecting.setTitle("传感器数据");
        collecting.setMessage("收集数据ing...");
        collecting.setCancelable(true);
        collecting.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        collecting.setButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelled = true;
                dialog.cancel();
                dialog.dismiss();
                Intent intent = new Intent(PredictAction.this, MainActivity.class);
                startActivity(intent);
            }
        });
        collecting.setMax(MAX_Progress);

        // create the MySensorManager object to collect sensor data
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        final MySensorManager ms = new MySensorManager(manager);


        // create the Handler to timer the collecting progress
        update_progress_dialog = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (cancelled)
                    return ;
                if (current_progress < MAX_Progress) {
                    ms.getSensorData(buffer, current_progress);
                    current_progress += 1;
                    collecting.setProgress(current_progress);
                    update_progress_dialog.postDelayed(update_thread, Progress_interval);
                } else {
                    collecting.dismiss();
                    Log.d("predict",dataToString() );
                    Toast.makeText(PredictAction.this, "你说你在做：" + doingWhat + "\n" + "我猜你是在做...", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private Runnable update_thread = new Runnable() {
        @Override
        public void run() {
            update_progress_dialog.sendEmptyMessage(1);
            update_progress_dialog.removeCallbacks(update_thread);
        }
    };

    public void getData(float[][] data, boolean wifiInfo, int volume) {
        data = buffer;
        wifiInfo = getWifiInfo();
        volume = getVolumeInfo();
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

    public boolean getWifiInfo() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        boolean status = false;
        if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
        {
            status = true;
        }
        return status;
    }


    // 获取媒体音量而已，还有系统音量，铃声等
    public int getVolumeInfo() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public static String getArrfHeader() {
        String header = "@relation test\n"
                + "@attribute label {0, 1, 2, 3, 4, 5}\n"
                + "@attribute wifi {true, false}\n"
                + "@attribute volume integer\n";

        for (int i = 0; i < SETTINGS.data_num_per_row; ++i)
            header += String.format("@attribute f%d real\n", i);
        header += "@data\n";

        return header;
    }

    public void WriteArrfFile(String fileName, String data, int mode) {
       FileOutputStream out;
       BufferedWriter buf = null;
       try {
           out = openFileOutput(fileName, mode);
           buf = new BufferedWriter(new OutputStreamWriter(out));
           buf.write(data);
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
           try {
               if (buf != null) {
                   buf.close();
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }
}
