package hw.happyjacket.com.ai_sensor_data;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by jacket on 2016/1/21.
 */
public class SensorDataProxy {
    private Context m_context;
    private int selectedAction;
    public String train_raw_file_name, test_raw_file_name;

    private static final int MAX_Progress = SETTINGS.MAX_Progress, Progress_interval = SETTINGS.Progress_interval;
    private  int current_progress = 0;
    private ProgressDialog collecting;
    private Handler update_progress_dialog;
    private SensorManager manager;
    private float[][] buffer = new float[MAX_Progress][MySensorManager.getDataLength()];
    private boolean cancelled = false;

    SensorDataProxy(Context context, int selectedAction) {
        this.m_context = context;
        this.selectedAction = selectedAction;
        train_raw_file_name = SETTINGS.FileHeader + SETTINGS.Raw_train_data_file;
        test_raw_file_name = SETTINGS.FileHeader + SETTINGS.Raw_test_data_file;

        // create the MySensorManager object to collect sensor data
        manager = (SensorManager) m_context.getSystemService(Context.SENSOR_SERVICE);

        // 进度对话框，用来指示当前收集的进度
        collecting = new ProgressDialog(m_context);
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
                Intent intent = new Intent(m_context, MainActivity.class);
                m_context.startActivity(intent);
            }
        });
    }

    public void Collecting() {
        final MySensorManager ms = new MySensorManager(manager);
        collecting.setMax(MAX_Progress);
        collecting.show();

        // Handler和Runnable配合，每隔0.1秒取一次数据，同时更新“进度对话框”一格
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
                    Toast.makeText(m_context, "收集完成", Toast.LENGTH_SHORT).show();
                    ms.Unregister();
                }
            }
        };
        update_progress_dialog.postDelayed(update_thread, Progress_interval);
    }

    private Runnable update_thread = new Runnable() {
        @Override
        public void run() {
            update_progress_dialog.sendEmptyMessage(1);
            update_progress_dialog.removeCallbacks(update_thread);
        }
    };


    // 将text的数据追加写入到文件fileName中（外部存储）
    public void save(String fileName, String text) {
        FileWriter fw;
        try {
            fw = new FileWriter(fileName, true);
            fw.write(text, 0, text.length());
            Log.d("data", text);
            fw.close();
            Toast.makeText(m_context, "数据文件保存在：" + fileName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ExternalStorage", "Error writing " + fileName);
            Toast.makeText(m_context, "Failed to open file " + fileName, Toast.LENGTH_SHORT).show();
        }
    }

    // 原始的做法：将每一帧都独立作为一个特征向量
    // PCA的做法：将50帧串联起来，每行有19*50 = 950维，压缩后剩下19维好像
    public String dataToString(boolean is_testing) {
        String start;
        if (is_testing)
            start = String.format("?,%b,%d", Utils.getWifiInfo(m_context), Utils.getVolumeInfo(m_context));
        else
            start = String.format("%d,%b,%d", selectedAction, Utils.getWifiInfo(m_context), Utils.getVolumeInfo(m_context));

        String ans = "";
        if (SETTINGS.feature == SETTINGS.FEATURE_TYPE.RAW_DATA) {
            for (int i = 0; i < buffer.length; ++i) {
                ans += start;
                for (int j = 0; j < buffer[i].length; ++j) {
                    ans += "," + buffer[i][j];
                }
                ans += '\n';
            }
            //Log.d("raw", ans);
        } else if (SETTINGS.feature == SETTINGS.FEATURE_TYPE.PCA) {
            ans = start;
            for (int i = 0; i < buffer.length; ++i) {
                for (int j = 0; j < buffer[i].length; ++j) {
                    ans += "," + buffer[i][j];
                }
            }
            ans += '\n';
            //Log.d("pca", ans);
        }
        return ans;
    }

    public void addOne() {
        Action action = new Action(m_context, "", selectedAction);
        action.addNum();
    }
}