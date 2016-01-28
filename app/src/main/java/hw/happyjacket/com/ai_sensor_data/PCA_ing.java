package hw.happyjacket.com.ai_sensor_data;


import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import Jama.Matrix;


public class PCA_ing {
	private static String[] headers;

	public static void Transform(String raw_file, String output) throws IOException {
		PCA pca = new PCA();

		// 获取原始数据
        int num_of_actions = 0;
		double[][] data = Utils.TransformToPCA(raw_file, headers, num_of_actions);

		// 均值中心化后的矩阵
		double[][] averageArray = pca.changeAverageToZero(data);
		Log.d("aver", "done");

		// 协方差矩阵
		double[][] varMatrix = pca.getVarianceMatrix(averageArray);
		Log.d("var", "done");

		// 特征值矩阵
		double[][] eigenvalueMatrix = pca.getEigenvalueMatrix(varMatrix);
		Log.d("feature", "done");

		// 特征向量矩阵
		double[][] eigenVectorMatrix = pca.getEigenVectorMatrix(varMatrix);
		Log.d("FV", "done");

		// 主成分矩阵
		Matrix principalMatrix = pca.getPrincipalComponent(data, eigenvalueMatrix, eigenVectorMatrix);
		Log.d("PM", "done");

		// 降维后的矩阵
		Matrix resultMatrix = pca.getResult(data, principalMatrix);
		Log.d("ALL finished", "done");

        // 准备输出结果
		FileOutputStream fos = new FileOutputStream(new File(output));
		PrintWriter osw = new PrintWriter(fos);
		BufferedWriter bw = new BufferedWriter(osw);

		data = resultMatrix.getArrayCopy();
		bw.write(Utils.getArffHeader());

		for (int i = 0; i < num_of_actions; ++i) {
			String text = headers[i];
			for (int j = 0; j < data[i].length; ++j)
				text += " " + data[i][j];
			text += '\n';
			bw.write(text);
		}

		//注意关闭的先后顺序，先打开的后关闭，后打开的先关闭
		bw.close();
		osw.close();
		fos.close();

		return ;
	}
}