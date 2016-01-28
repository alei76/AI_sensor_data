package hw.happyjacket.com.ai_sensor_data;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class PredictAction extends AppCompatActivity {
    private int selectedAction;
    private SensorDataProxy proxy;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int label = (int)msg.obj;
            Toast.makeText(PredictAction.this,
                    String.format("你说你在%s\n我猜你在%s", SETTINGS.actionLabels[selectedAction], SETTINGS.actionLabels[label]),
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PredictAction.this, MainActivity.class);
            startActivity(intent);
        }
    };

    private Runnable update_thread = new Runnable() {
        @Override
        public void run() {
           //String testFileName = getExternalFilesDir(null) + File.separator + SETTINGS.Test_ARRF_File;
            String test_raw_file = proxy.test_raw_file_name;
            proxy.save(test_raw_file, proxy.dataToString(true));
            // 保存好test_raw_file之后，记得将这个数据追加到已有的train数据后面，这样数据集会越来越大，结果也会越可信
            proxy.save(proxy.train_raw_file_name, proxy.dataToString(false));
            proxy.addOne();

            // 开始训练，获取预测的类别
            int label = Testing.Test();
            Message msg = new Message();
            msg.obj = label;
            handler.sendMessage(msg);
            handler.removeCallbacks(update_thread);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict_action);

        Intent intent = getIntent();
        selectedAction = intent.getIntExtra("selectedAction", 0);
        final String doingWhat = intent.getStringExtra("doing");
        proxy = new SensorDataProxy(PredictAction.this, selectedAction);

        final Button start = (Button) findViewById(R.id.startPredict);
        final Button ok = (Button) findViewById(R.id.I_am_ok);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proxy.Collecting();
                start.setVisibility(View.INVISIBLE);
                ok.setVisibility(View.VISIBLE);
            }
        });


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(update_thread, 0);
                ok.setVisibility(View.INVISIBLE);
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
    }

   /* public void WriteArffFile(String fileName, String data, int mode) {
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
    }*/
}
