package hw.happyjacket.com.ai_sensor_data;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * Created by jacket on 2016/1/21.
 */
public class Testing {
    public static int Test() {
        // 测试之前的准备：先将原始数据（raw data）抽取出特征并转化为ARFF格式的文件
        try {
            if (SETTINGS.feature == SETTINGS.FEATURE_TYPE.RAW_DATA) {
                Utils.TransformRawToArff(SETTINGS.FileHeader + SETTINGS.Raw_test_data_file, SETTINGS.FileHeader + SETTINGS.Test_ARRF_File);
            }
            else if (SETTINGS.feature == SETTINGS.FEATURE_TYPE.PCA) {
                PCA_ing.Transform(SETTINGS.FileHeader + SETTINGS.Raw_test_data_file, SETTINGS.FileHeader + SETTINGS.Test_ARRF_File);
            }
            Log.d("Testing...", "OK!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 加载测试数据（ARFF格式的）
        File inputFile = new File(SETTINGS.FileHeader + SETTINGS.Test_ARRF_File);
        ArffLoader atf = new ArffLoader();
        try {
            atf.setFile(inputFile);
            // 读入测试文件
            Instances instancesTest = atf.getDataSet();
            // 设置分类属性所在行号（第一行为0号），instancesTest.numAttributes()可以取得属性总数
            instancesTest.setClassIndex(0);

            // 加载已经保存好的分类器模型——Naive Bayes
            Classifier m_classifier = (Classifier) weka.core.SerializationHelper.read(SETTINGS.FileHeader + SETTINGS.Model_File);

            // 统计分类的结果
            int count[] = new int[SETTINGS.actionLabels.length];
            for (int i = 0; i < instancesTest.numInstances(); ++i) {
                // 分类得出类别
                double label = m_classifier.classifyInstance(instancesTest.instance(i));
                Log.d("Sample " + i, label + " " + instancesTest.numAttributes() + " " + instancesTest.numClasses());
                ++count[(int)label];
            }

            // 每个动作50行数据，每行数据有一个类别，统计得出最多的那个类别作为最终的结果
            int label = 0, number = 0;
            for (int i = 0; i < count.length; ++i)
                if (count[i] > number) {
                    number = count[i];
                    label = i;
                }

            Log.d("number: " + number, "label: " + label);
            return label;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
