package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class FileRead {
	// ���ж�ȡ�ļ�
	public static String readLineVarFile(String fileName, int lineNumber)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(fileName)));
		String line = reader.readLine();
		if (lineNumber < 0 || lineNumber > getTotalLines(fileName)) {
			System.out.println("�����ļ���������Χ֮�ڡ�");
		}
		int num = 0;
		String latlanStr = null;
		while (line != null) {
			if (lineNumber == ++num) {
				latlanStr = line;
			}
			line = reader.readLine();
		}
		reader.close();
		return latlanStr;
	}

	// �ļ����ݵ���������
	public static int getTotalLines(String fileName) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(fileName)));
		LineNumberReader reader = new LineNumberReader(in);
		String s = reader.readLine();
		int lines = 0;
		while (s != null) {
			lines++;
			s = reader.readLine();
		}
		reader.close();
		in.close();
		return lines;
	}

	// ��ȡĳ�ļ����������ļ�
	public static String[] readfile(String filepath)
			throws FileNotFoundException, IOException {
		File file = new File(filepath);
		String[] filelist = file.list();
		try {
			if (!file.isDirectory()) {
				System.out.println("�ļ�");
				System.out.println("path=" + file.getPath());
				System.out.println("absolutepath=" + file.getAbsolutePath());
				System.out.println("name=" + file.getName());

			} else if (file.isDirectory()) {
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(filepath + "\\" + filelist[i]);
					if (!readfile.isDirectory()) {
						filelist[i] = readfile.getName();
					} else if (readfile.isDirectory()) {
						readfile(filepath + "\\" + filelist[i]);
					}
				}

			}

		} catch (FileNotFoundException e) {
			System.out.println("readfile()   Exception:" + e.getMessage());
		}
		return filelist;
	}

}
