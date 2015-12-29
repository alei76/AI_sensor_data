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
    private final int sensorTypes[] = {Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GRAVITY, Sensor.TYPE_LIGHT, Sensor.TYPE_GYROSCOPE, Sensor.TYPE_LINEAR_ACCELERATION ,
            Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_ROTATION_VECTOR};
    static final int sensorDataLength[] = {3, 3, 1, 3, 3, 3, 3};

    MySensorManager(SensorManager sm) {
        manager = sm;
        sensorData = new float[sensorTypes.length][3];
        sensorList = new ArrayList<>();
        listenerList = new ArrayList<>();

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

    private String valueToString(float[] values) {
        String s = new String();
        for (int i = 0; i < values.length; ++i) {
            s += String.format("%f", values[i]);
        }
        return s;
    }

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
