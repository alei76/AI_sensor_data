package hw.happyjacket.com.ai_sensor_data;

import android.hardware.Sensor;

/**
 * Created by jacket on 2016/1/21.
 */
public class SETTINGS {
    //public static final String [] actionLabels = {"跑步", "走路", "跳跃", "休息中", "上楼梯", "下楼梯", "仰卧起坐", "俯卧撑"};
    public static final String [] actionLabels = {"跑步", "走路", "跳跃", "休息中", "上楼梯", "下楼梯"};
    public static final int frame_of_action = 50, data_num_per_row = 19, num_of_action = actionLabels.length;
    public static final int MAX_Progress = 50, Progress_interval = 100;
    public static final int sensorTypes[] = {Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GRAVITY, Sensor.TYPE_LIGHT, Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_LINEAR_ACCELERATION , Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_ROTATION_VECTOR};
    public static final int sensorDataLength[] = {3, 3, 1, 3, 3, 3, 3};
    public static final String Raw_train_data_file = "train_raw_data.txt", Raw_test_data_file = "test_raw_data.txt";
    public static final String Train_ARRF_File = "train.txt", Test_ARRF_File = "test.txt";
    public static final String Model_File = "RF.model";
    public static final String SharePref_pre = "HappySports";
    public static final String Action_num_shareP_file = "HappySports";
    public static int max_num_actions = 60, PCA_row_size = frame_of_action * data_num_per_row;
    public static final String Assets_file_path = Model_File;
    public static String FileHeader = "";
    public static int Model = 0;
    public static final boolean DEBUG = false;
    public enum FEATURE_TYPE {
        RAW_DATA, PCA
    }
    public static FEATURE_TYPE feature = FEATURE_TYPE.RAW_DATA;
}
