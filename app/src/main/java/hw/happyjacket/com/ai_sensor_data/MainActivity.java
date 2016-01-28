package hw.happyjacket.com.ai_sensor_data;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Action> AllActions;
    private int selectedAction = 0, totalSamples = 0;

    // 与Runnable配合，负责新开线程训练数据
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(MainActivity.this, "训练已完成，可以开始预测了", Toast.LENGTH_SHORT).show();
        }
    };
    private Runnable update_thread = new Runnable() {
        @Override
        public void run() {
            new Training(MainActivity.this);
            handler.sendEmptyMessage(1);
            handler.removeCallbacks(update_thread);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        genData();
        SETTINGS.max_num_actions = totalSamples + 30;   // 初始化最大的数量
        SETTINGS.FileHeader = getExternalFilesDir(null) + File.separator;

        if (!SETTINGS.DEBUG) {
            // 将训练好的模型放到asset目录下，然后在首次使用的时候将其读取出来~
            String modelFileName = SETTINGS.FileHeader + SETTINGS.Model_File;
            File file = new File(modelFileName);
            if (!file.exists())
                Utils.copyFilesFassets(this, SETTINGS.Assets_file_path, modelFileName);
            else
                Log.d("File exists", modelFileName);

            /*String trainFileName = getExternalFilesDir(null) + File.separator + SETTINGS.Train_ARRF_File;
            SensorDataProxy.InsureArffFileHeader(this, trainFileName, false);*/
        }

        // 开始收集按钮
        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("CollectSensorData");
                intent.putExtra("selectedAction", selectedAction);
                startActivity(intent);
            }
        });

        // 预测按钮
        Button predict = (Button) findViewById(R.id.predict);
        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("PredictAction");
                intent.putExtra("selectedAction", selectedAction);
                //intent.putExtra("doing", actionLabels[selectedAction]);
                intent.putExtra("doing", AllActions.get(selectedAction).getLabel());
                startActivity(intent);
            }
        });

        // 训练数据的按钮
        final Button train = (Button) findViewById(R.id.train);
        train.setText(String.format("训练(%d数据)", totalSamples));
        train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("训练模型中...");
                dialog.setMessage("请耐心等待~");
                dialog.setNegativeButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                handler.postDelayed(update_thread, 0);
            }
        });


        /*final Button model = (Button) findViewById(R.id.Model);
        model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SETTINGS.Model == 0) {
                    SETTINGS.Model = 1;
                    model.setText("PCA");
                    train.setVisibility(View.INVISIBLE);
                    SETTINGS.feature = SETTINGS.FEATURE_TYPE.PCA;
                } else {
                    SETTINGS.Model = 0;
                    model.setText("原始数据");
                    SETTINGS.feature = SETTINGS.FEATURE_TYPE.RAW_DATA;
                    train.setVisibility(View.VISIBLE);
                }
            }
        });*/


        // 动作列表及其适配器
        ActionAdapter adapter = new ActionAdapter(MainActivity.this, R.layout.action_item, AllActions);

        final ListView action_list = (ListView) findViewById(R.id.action_list);
        action_list.setSelection(selectedAction);
        action_list.setAdapter(adapter);
        action_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedAction = position;
                Toast.makeText(MainActivity.this, "选择动作：" + AllActions.get(selectedAction).getLabel(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 初始化动作列表的数据
    public void genData() {
        AllActions = new ArrayList<Action>();
        for (int i = 0; i < SETTINGS.actionLabels.length; ++i) {
            Action action = new Action(this, SETTINGS.actionLabels[i], i);
            AllActions.add(action);
            totalSamples += action.getNum();
        }
    }
}
