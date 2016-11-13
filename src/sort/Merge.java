package sort;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.SequenceInputStream;

import util.Configuration;
import util.FileRead;

public class Merge {
//	static String rootDirectory = "E:\\SUN\\WORKSPACE\\DATASET\\Geolife\\";
//	public static final String prePath = rootDirectory + "xyGPS\\";

	public static void mergeFile(int nodeNum) throws IOException {

		String latPathStr = "";
		int latPath;

		System.out.println("[ͬ��켣�ϲ�]");
		
		for (int m = 0; m < nodeNum; m++) {// ѭ�����ÿ���ڵ��ڵ������ļ�
			System.out.println("�ڵ�" + m + "��");
			latPath = m;
			if (latPath / 10 == 0)
				latPathStr = "00" + Integer.toString(latPath);
			else if (latPath / 100 == 0)
				latPathStr = "0" + Integer.toString(latPath);
			else
				latPathStr = Integer.toString(latPath);

			String path = Configuration.getConfiguration().getXYDirectory() + latPathStr;

			File file = new File(path);
			String[] filelist = file.list();
			String[] fileName = new String[filelist.length + 1]; // �ļ�����ʼ��
			fileName = FileRead.readfile(path); // ��ȡ�����ļ������ִ����ַ�������

			for (int i = 0; i < fileName.length - 1; i++) {// ��ÿ���ļ����м�飬�����ͬһ��������ļ����ϲ�
				if (fileName[i].substring(0, 8).equals(
						fileName[i + 1].substring(0, 8))) {
					FileReader file1 = new FileReader(path + "\\"
							+ fileName[i + 1]);
					FileWriter file2 = new FileWriter(path + "\\" + fileName[i],
							true);
					BufferedReader br1 = new BufferedReader(file1);
					BufferedWriter bw1 = new BufferedWriter(file2);

					String s;

					s = br1.readLine();
					while (s != null) {
						bw1.write(s);
						bw1.newLine();
						s = br1.readLine();
					}
					bw1.close();
					br1.close();
					File deleteF = new File(path + "\\" + fileName[i + 1]);
					deleteF.delete();
				}
			}
			System.out.println("�ڵ�" + m + "�ϲ���ϡ�");

		}
	}
}
