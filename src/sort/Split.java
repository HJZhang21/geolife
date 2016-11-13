package sort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import util.Configuration;
import util.FileRead;

public class Split {

//	static String rootDirectory = "E:\\SUN\\WORKSPACE\\DATASET\\Geolife\\";
//	public static final String prePath = rootDirectory + "xyGPS\\";
	// static String[] dataForOneFile; // 一个节点的一个.plt文件中的数据
	// static String[] dataRestForOneFile;
	


	public static void splitFile(int nodeNum) throws IOException {
		String latPathStr = "";
		int latPath;

		System.out.println("[跨天轨迹分割]");
		
		for (int m = 0; m < nodeNum; m++) {// 循环检查各个节点内数据
			System.out.println("节点" + m + "：");
			int lineNumber = 1;
			int totalDataLines = 0; // 一个数据文件的总行数
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
			String[] fileName = new String[filelist.length + 1]; // 文件名初始化
			fileName = FileRead.readfile(path); // 获取所有文件的名字存入字符串数组

			for (int i = 0; i < fileName.length; i++) {// 检查每个文件内数据有没有跨天的
				totalDataLines = FileRead.getTotalLines(path + "\\"
						+ fileName[i]);// 获取一个数据文件的总行数
				String[] dataForOneFile = new String[totalDataLines];
				for (int j = 0; j < dataForOneFile.length - 1; j++) {
					dataForOneFile[j] = FileRead.readLineVarFile(path + "\\"
							+ fileName[i], lineNumber + j);// 读取某一数据文件指定行
					dataForOneFile[j + 1] = FileRead.readLineVarFile(path + "\\"
							+ fileName[i], lineNumber + j + 1);
					if (dataForOneFile[j].charAt(9) != dataForOneFile[j + 1]
							.charAt(9)) {
						System.out.print("...第" + j + "行");
						StartSplit(j + 2, fileName[i], dataForOneFile[j + 1],
								path, latPathStr);
						File deleteF = new File(path + "\\" + fileName[i]);
						deleteF.delete();
						String filePath = Configuration.getConfiguration().getXYDirectory() + latPathStr + "\\"
								+ fileName[i];
						for (int k = 0; k < j + 1; k++) {
							File writeFile = new File(filePath);
							BufferedWriter out = new BufferedWriter(
									new FileWriter(writeFile, true));
							out.write(dataForOneFile[k]);
							out.newLine();
							out.close();
							out = null;
							writeFile = null;
						}
						break;
					}
				}

			}
			System.out.println("节点" + m + "分割完毕。");

		}

	}

	/**
	 * 分开文件内跨天数据，并创建新文件用以储存
	 * 
	 * @param line
	 * @param fileName
	 * @param newFileName
	 * @param path
	 * @param latPathStr
	 * @throws IOException
	 */
	private static void StartSplit(int line, String fileName,
			String newFileName, String path, String latPathStr)
			throws IOException {
		// TODO Auto-generated method stub
		File file = new File(path);
		String[] filelist = file.list();
		int totalDataLines = FileRead.getTotalLines(path + "\\" + fileName);// 获取一个数据文件的总行数
		String[] dataRestForOneFile = new String[totalDataLines - line];
		for (int j = 0; j < dataRestForOneFile.length; j++) {
			dataRestForOneFile[j] = FileRead.readLineVarFile(path + "\\"
					+ fileName, line + j);// 读取某一数据文件指定行
			// 把文件名改为每个文件数据第一行的北京时间

			String newName = newFileName.substring(0, 19);
			newName = newName.trim();
			String nName = "";
			if (newName != null && !"".equals(newName)) {
				for (int i = 0; i < newName.length(); i++) {
					if (newName.charAt(i) >= 48 && newName.charAt(i) <= 57) {
						nName += newName.charAt(i);
					}
				}

			}
			nName += ".plt";

			String filePath = Configuration.getConfiguration().getXYDirectory() + latPathStr + "\\" + nName;
			File writeFile = new File(filePath);
			BufferedWriter out = new BufferedWriter(new FileWriter(writeFile,
					true));
			out.write(dataRestForOneFile[j]);
			out.newLine();
			out.close();
			out = null;
			writeFile = null;

		}

	}
}
