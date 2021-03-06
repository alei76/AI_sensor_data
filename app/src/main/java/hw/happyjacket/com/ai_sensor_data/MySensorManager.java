package hw.happyjacket.com.ai_sensor_data;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacket on 2015/12/27.
 */
public class MySensorManager {
    private SensorManager manager;
    private ArrayList<Sensor> sensorList;
    private ArrayList<SensorEventListener> listenerList;
    private float [][]  sensorData;
    private final int sensorTypes[] = SETTINGS.sensorTypes;
    static final int sensorDataLength[] = SETTINGS.sensorDataLength;

    MySensorManager(SensorManager sm) {
        manager = sm;
        sensorData = new float[sensorTypes.length][3];
        sensorList = new ArrayList<>();
        listenerList = new ArrayList<>();

        // 生成各种需要用到的传感器对象
        for (int i = 0; i < sensorTypes.length; ++i) {
            Sensor ss = manager.getDefaultSensor(sensorTypes[i]);
            if (ss == null) {
                Log.d("No such sensor", ""+i);
            }
            sensorList.add(ss);
            final int tmpId = i;
            SensorEventListener tmp = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    sensorData[tmpId] = event.values;
                    //Log.d("sm", tmpId + ": " + valueToString(event.values));
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
            listenerList.add(tmp);
        }

        // 注册监听各种传感器
        Register(listenerList);
    }

    public void Register(List<SensorEventListener> listeners) {
        for (int i = 0; i < listeners.size(); ++i) {
            manager.registerListener(listeners.get(i), sensorList.get(i), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void Unregister() {
        for (int i = 0; i < listenerList.size(); ++i) {
            manager.unregisterListener(listenerList.get(i), sensorList.get(i));
        }
    }

    // 将当前的传感器数据存入到buffer的指定列（position）
    public void getSensorData(float[][] buffer, int position) {
        int index = 0;
        for (int i = 0; i < sensorData.length; ++i) {
            for (int j = 0; j < sensorDataLength[i]; ++j) {
                buffer[position][index++] = sensorData[i][j];
            }
        }
    }

    public static int getDataLength() {
        int ans = 0;
        for (int i = 0; i < sensorDataLength.length; ++i)
            ans += sensorDataLength[i];
        return ans;
    }
}
