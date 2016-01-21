package hw.happyjacket.com.ai_sensor_data;

import android.hardware.Sensor;

/**
 * Created by jacket on 2016/1/21.
 */
public class SETTINGS {
    public static final int frame_of_action = 50, data_num_per_row = 19, num_of_action = 6;
    public static final int MAX_Progress = 50, Progress_interval = 100;
    public static final int sensorTypes[] = {Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GRAVITY, Sensor.TYPE_LIGHT, Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_LINEAR_ACCELERATION , Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_ROTATION_VECTOR};
    public static final int sensorDataLength[] = {3, 3, 1, 3, 3, 3, 3};
}
