package hw.happyjacket.com.ai_sensor_data;

import android.content.Context;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jacket on 2016/1/27.
 */
public class Utils {
    public static String getArffHeader() {
        String header = "@relation test\n"
                + "@attribute label {0, 1, 2, 3, 4, 5}\n"
                + "@attribute wifi {true, false}\n"
                + "@attribute volume integer\n";

        for (int i = 0; i < SETTINGS.data_num_per_row; ++i)
            header += String.format("@attribute f%d real\n", i);
        header += "@data\n";

        return header;
    }

    /**
     *  从assets目录中复制整个文件夹内容
     *  @param  context  Context 使用CopyFiles类的Activity
     *  @param  oldPath  String  原文件路径  如：/aa
     *  @param  newPath  String  复制后路径  如：xx:/bb/cc
     */
    public static void copyFilesFassets(Context context, String oldPath, String newPath) {
        try {
            InputStream is = context.getAssets().open(oldPath);
            FileOutputStream fos = new FileOutputStream(new File(newPath));
            byte[] buffer = new byte[1024];
            int byteCount;
            while((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
            }
            fos.flush();//刷新缓冲区
            is.close();
            fos.close();
            Log.d("assets", "good");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 如果文件不存在，则返回null指针
    public static File IsFileExists(String fileName) {
        File f = new File(fileName);
        if (f.exists())
            return f;
        else
            return null;
    }

    // 获取系统的WIFI信息
    public static boolean getWifiInfo(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean status = false;
        if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
            status = true;
        return status;
    }

    // 获取媒体音量而已，除此之外还有系统音量，铃声等
    public static int getVolumeInfo(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    // 确保所指定的文件必定是以ARRF格式的头部存在！
   /* public static void InsureArffFileHeader(Context context, String fileName) {
        FileWriter fw = null;
        try {
            File f = Utils.IsFileExists(fileName);
            if (f != null) {
                fw = new FileWriter(fileName, false);
                String header = Utils.getArffHeader();
                fw.write(header, 0, header.length());
                Log.d("Create header", fileName);
            } else
                Log.d("File exists", fileName);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public static double[][] TransformToPCA(String fileName, String[] headers, int num_of_actions) throws IOException {
        int size = SETTINGS.PCA_row_size, max_num_actions = SETTINGS.max_num_actions;
        double[][] ans = new double[max_num_actions][size];
        headers = new String[max_num_actions];
        int row = 0, col = 0;

        FileInputStream fis = new FileInputStream(fileName);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null)
        {
            String data[] = line.split(",");
            for (int i = 3; i < data.length; ++i) {
                ans[row][col++] = Double.valueOf(data[i]);
            }

            headers[row] = String.format("%s,%s,%s", data[0], data[1], data[2]);

            if (col >= size) {
                ++row;
                col = 0;
            }
        }

        num_of_actions = row;
        double[][] data = new double[row][size];

        Log.d("Read ready", "" + num_of_actions);

        for (int i = 0; i < row; ++i)
            for (int j = 0; j < size; ++j)
                data[i][j] = ans[i][j];

        return data;
    }

    public static void TransformRawToArff(String input, String output) {
        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(input));
            writer = new BufferedWriter(new FileWriter(output));
            writer.write(Utils.getArffHeader());
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + '\n');
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
