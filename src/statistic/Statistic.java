package statistic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Statistic {

	static String prePath = "e:/xyGPS/";
	static boolean[][] onLinePerson = new boolean[182][365];// 此处默认设为一年,182个节点


	public static void statisticFile(int nodeNum,int DAYS) throws IOException, ParseException {

		int latPath;
		String latPathStr = "";

		System.out.println("请输入开始日期，格式为yyyy-MM-dd");
		Scanner input = new Scanner(System.in);
		String in = input.nextLine();
		in = in + " 00:00:00";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date startDate = new Date();
		startDate = format.parse(in);
		long startTime = startDate.getTime();

		for (int i = 0; i < nodeNum; i++) {
			System.out.println(i + "文件夹");
			latPath = i;
			if (latPath / 10 == 0)
				latPathStr = "00" + Integer.toString(latPath);
			else if (latPath / 100 == 0)
				latPathStr = "0" + Integer.toString(latPath);
			else
				latPathStr = Integer.toString(latPath);

			String path = prePath + latPathStr;

			File file = new File(path); // 文件初始化
			String[] filelist = file.list();
			String[] fileName = new String[filelist.length + 1]; // 文件名初始化
			try {
				fileName = FileRead.readfile(path); // 获取所有文件的名字存入字符串数组
			} catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			String firstLineOneFile = null;
			String firstDate;
			Date[] date = new Date[fileName.length];
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for (int j = 0; j < fileName.length; j++) {
				firstLineOneFile = FileRead.readLineVarFile(path + "/"
						+ fileName[j], 1);
				firstDate = firstLineOneFile.substring(0, 10);
				date[j] = sdf.parse(firstDate);
				long subtract = date[j].getTime() - startTime;
				int subDays = (int) (subtract / 1000 / 3600 / 24);
				onLinePerson[i][subDays] = true;
			}
			for (int k = 0; k < DAYS; k++)
				System.out.println(onLinePerson[i][k]);
			// 统计同一天在线节点个数

		}

		writeFileMatrix(onLinePerson, DAYS, nodeNum);

	}

	/**
	 * 把二维数组以01形式写入文件
	 * 
	 * @param onLinePerson
	 * @param dAYS
	 * @param nodeNum
	 * @throws IOException
	 */
	private static void writeFileMatrix(boolean[][] onLinePerson, int dAYS,
			int nodeNum) throws IOException {
		// TODO Auto-generated method stub
		String filePath = "e:\\onLineMatrix\\data.txt";
		File writeFile = new File(filePath);
		BufferedWriter out = new BufferedWriter(new FileWriter(writeFile, true));
		for (int i = 0; i < nodeNum; i++) {
			for (int j = 0; j < dAYS; j++) {

				if (onLinePerson[i][j])
					out.write(1 + " ");
				else {
					out.write(0 + " ");
				}
			}
			out.newLine();
		}
		out.close();
		out = null;
		writeFile = null;
	}

}
