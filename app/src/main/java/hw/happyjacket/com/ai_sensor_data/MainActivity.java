package hw.happyjacket.com.ai_sensor_data;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
//    private String provider;
//    private String[] data = { "Apple", "Banana", "Orange", "Watermelon",
//            "Pear", "Grape", "Pineapple", "Strawberry", "Cherry", "Mango" };
//    private List<Fruit> fruitList = new ArrayList<Fruit>();

    final int MAX_Progress = 50, Progress_interval = 100;
    int current_progress = 0;

    ProgressDialog collecting;
    private TextView positionTextView;
    private LocationManager locationManager;
    private SensorManager sensorManager;
    private TextView wifiInfo;
    MySensorManager ms;
    private EditText edit;
    private Button ok_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


       /* Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER );
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);*/

        //ms = new MySensorManager(sensorManager);

        wifiInfo = (TextView) findViewById(R.id.wifi_info);
        wifiInfo.setText(getWifiInfo() + " " + getVolumeInfo());
        edit = (EditText) findViewById(R.id.edit);
        ok_button = (Button) findViewById(R.id.ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = edit.getText().toString();
                save(inputText);
                Log.d("233", inputText);
            }
        });

        collecting = new ProgressDialog(MainActivity.this);
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

        //update_progress_dialog.postDelayed(update_thread, Progress_interval);

        //       initFruits();

        //// ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, data);

        //FruitAdapter adapter = new FruitAdapter(MainActivity.this, R.layout.fruit_item, fruitList);

        //ListView listView = (ListView)findViewById(R.id.list_view);

        //listView.setAdapter(adapter);

        //listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        //@Override

        //public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Fruit fruit = fruitList.get(position);

        //Toast.makeText(MainActivity.this, fruit.getName(), Toast.LENGTH_SHORT).show();

        //}

        //});

        // 测试GPS管理
        //positionTextView = (TextView)findViewById(R.id.position_text_view);
        //// What is Context?
        //locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //List<String> providerList = locationManager.getProviders(true);
        //if (providerList.contains(LocationManager.GPS_PROVIDER)) {
        //provider = LocationManager.GPS_PROVIDER;
        //} else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
        //provider = LocationManager.NETWORK_PROVIDER;
        //} else {
        //Toast.makeText(this, "No location provider to use", Toast.LENGTH_LONG).show();
        //return ;
        //}
        //
        //Location location = locationManager.getLastKnownLocation(provider);
        //if (location != null) {
        //showLocation(location);
        //}
        //locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
    }

    private Handler update_progress_dialog = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (current_progress < MAX_Progress) {
                collecting.setProgress(current_progress);
                update_progress_dialog.postDelayed(update_thread, Progress_interval);
            } else {
                collecting.dismiss();
                Toast.makeText(MainActivity.this, "done", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private Runnable update_thread = new Runnable() {
        @Override
        public void run() {
            current_progress += 1;
            update_progress_dialog.sendEmptyMessage(1);
            update_progress_dialog.removeCallbacks(update_thread);
        }
    };

    private String getWifiInfo() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String status = "WIFI_STATE_UNENABLED";
        if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
        {
            status = "WIFI_STATE_ENABLED";
        }

        return status;
    }

    // 获取媒体音量而已，还有系统音量，铃声等
    private int getVolumeInfo() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /*private void getCameraInfo() {
        int cameraId = 0;
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
    }*/


/*    void initFruits() {
        Fruit apple = new Fruit("Apple", R.drawable.apple);
        fruitList.add(apple);
        Fruit banana = new Fruit("Banana", R.drawable.banana);
        fruitList.add(banana);
        Fruit orange = new Fruit("Orange", R.drawable.orange);
        fruitList.add(orange);
        Fruit watermelon = new Fruit("Watermelon", R.drawable.watermelon);
        fruitList.add(watermelon);
        Fruit pear = new Fruit("Pear", R.drawable.pear);
        fruitList.add(pear);
        Fruit grape = new Fruit("Grape", R.drawable.grape);
        fruitList.add(grape);
        Fruit pineapple = new Fruit("Pineapple", R.drawable.pineapple);
        fruitList.add(pineapple);
        Fruit strawberry = new Fruit("Strawberry", R.drawable.strawberry);
        fruitList.add(strawberry);
        Fruit cherry = new Fruit("Cherry", R.drawable.cherry);
        fruitList.add(cherry);
        Fruit mango = new Fruit("Mango", R.drawable.mango);
        fruitList.add(mango);
    }*/

    private void save(String text) {
        File file = new File(getExternalFilesDir(null), "hello.txt");
        try {
            OutputStream os = new FileOutputStream(file);
            os.write(text.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ExternalStorage", "Error writing " + file, e);
        }
    }

    protected void onDestroy() {
        //ms.Unregister();
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
        }
    };

    private void showLocation(Location location) {
        String currentPosition = "latitude is " + location.getLatitude() + "\n" + "longitude is " + location.getLongitude();
        positionTextView.setText(currentPosition);
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
