package hw.happyjacket.com.ai_sensor_data;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;


/**
 * Created by jacket on 2016/1/21.
 */
public class Training {
    private Context m_context;
    private String header;

    Training(Context context) {
        m_context = context;
        header = m_context.getExternalFilesDir(null) + File.separator;

        // 训练之前的准备：先将原始数据（raw data）抽取出特征并转化为ARFF格式的文件
        try {
            if (SETTINGS.feature == SETTINGS.FEATURE_TYPE.RAW_DATA) {
                Utils.TransformRawToArff(SETTINGS.FileHeader + SETTINGS.Raw_train_data_file, SETTINGS.FileHeader + SETTINGS.Train_ARRF_File);
            }
            else if (SETTINGS.feature == SETTINGS.FEATURE_TYPE.PCA) {
                PCA_ing.Transform(SETTINGS.FileHeader + SETTINGS.Raw_train_data_file, SETTINGS.FileHeader + SETTINGS.Train_ARRF_File);
            }
            Log.d("Training...", "OK!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 开始训练
        Train();
    }

    public void Train() {
        // 分类器——选择Navie Bayes
        //Classifier m_classifier = new NaiveBayes();
        Classifier m_classifier = new RandomForest();

        // 训练数据
        File inputFile = new File(header + SETTINGS.Train_ARRF_File);
        ArffLoader atf = new ArffLoader();
        try {
            atf.setFile(inputFile);
            // 读入训练文件
            Instances instancesTrain = atf.getDataSet();
            // 设置分类属性所在行号（第一行为0号）
            instancesTrain.setClassIndex(0);

            try {
                // 训练模型
                m_classifier.buildClassifier(instancesTrain);
                // 保存模型（需要写外部存储）
                SerializationHelper.write(header + SETTINGS.Model_File, m_classifier);
                Log.d("write", "good");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("train", "good");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}