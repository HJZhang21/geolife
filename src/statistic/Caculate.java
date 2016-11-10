package statistic;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.IntPredicate;

public class Caculate {
	static int allNodes;
	static int givenNodes;
	static int givenDays;
	static int dAYS;
	static int[] all = new int[allNodes];
	static int[] given;
	static String pickedNodes = "";
//	static String rootDirectory = "E:\\";
	static String rootDirectory = "E:\\SUN\\WORKSPACE\\DATASET\\Geolife\\";

	public Caculate(int allNodes,int dAYS){
		this.allNodes=allNodes;
		this.dAYS=dAYS;
	}
	public static void caculateData() throws IOException, ParseException {
		System.out.println("请选择您要找出的类型：1.给定节点找最长时间 2.给定连续天数找最多节点");

		Scanner input = new Scanner(System.in);
		switch (input.nextInt()) {
		case 1:
			System.out.println("请输入给定的节点数");
			Scanner input1 = new Scanner(System.in);
			givenNodes = input1.nextInt();
			if (givenNodes > allNodes) {
				System.out.println("给定节点数大于总节点数");
				return;
			}
			given = new int[givenNodes];
			Zuhe(allNodes, givenNodes);
			longestDays(pickedNodes);
			break;
		case 2:
			System.out.println("请输入给定的持续天数");
			Scanner input2 = new Scanner(System.in);
			givenDays = input2.nextInt();
			getMostNodes(givenDays);
			break;
		default:
			System.out.println("输入有误");
			break;
		}
	}

	/**
	 * 得到给定持续天数的最多节点数
	 * 
	 * @param givenDays2
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	private static void getMostNodes(int givenDays2) throws IOException,
			ParseException {
		// TODO Auto-generated method stub
		int[][] onLineMatrix = readFileMatrix(dAYS);
		int countOne = 0;
		int countGroup = 0;
		int maxNodes = 0;
		List<Integer> listk = new ArrayList<Integer>();
		List<Integer> listj = new ArrayList<Integer>();
		for (int k = 0; k < dAYS; k++) {
			for (int j = 0; j < allNodes; j++) {
				for (int i = k; i < k + givenDays2; i++) {
					if (onLineMatrix[j][i] == 1) {
						countOne++;
					} else {
						countOne = 0;
						break;
					}
					if (i == k + givenDays2 - 1 && countOne == givenDays2) {
						countGroup++;
						listk.add(k);
						listj.add(j);
						if (maxNodes < countGroup) {
							maxNodes = countGroup;
						}
						countOne = 0;
					}
				}
			}
			countGroup = 0;
			countOne = 0;

		}
		int mostFrequency = countFrequency(listk);
		long subtractDays = mostFrequency;
		int[] pickedNodes = listKgetPickedNodes(listk, listj, mostFrequency,
				maxNodes);
		System.out.print("被选中的节点为");
		for(int i=0;i<pickedNodes.length;i++){
			System.out.print(pickedNodes[i]);
		}
		System.out.println();
		System.out.println("请输入开始日期，格式为yyyy-MM-dd");
		Scanner input = new Scanner(System.in);
		String in = input.nextLine();
		in = in + " 00:00:00";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date startDate = new Date();
		startDate = format.parse(in);
		long startTime = startDate.getTime();
		long maxNodesStartTime = startTime + subtractDays * 24 * 3600 * 1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String time = sdf.format(new Date((maxNodesStartTime)));
		String maxNodesStartDate = "";
		int index = 0;
		while (time.charAt(index) != ' ') {
			maxNodesStartDate += time.charAt(index);
			index++;
		}
		if(maxNodes==0){
			System.out.println("没有满足要求的节点");
		}
		takeOutFiles2(pickedNodes, maxNodes, maxNodesStartDate);
	}

	/**
	 * 把有持续天数的日期文件依次写入新的文件夹中
	 * 
	 * @param pickedNodes2
	 * @param maxNodes
	 * @param maxNodesStartDate
	 * @throws FileNotFoundException
	 * @throws IOException
	 */

	private static void takeOutFiles2(int[] pickedNodes2, int maxNodes,
			String maxNodesStartDate) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		int latName;
		String nodeName;
		for (int m = 0; m < pickedNodes2.length; m++) {
			latName = pickedNodes2[m];
			if (latName / 10 == 0)
				nodeName = "00" + Integer.toString(latName);
			else if (latName / 100 == 0)
				nodeName = "0" + Integer.toString(latName);
			else
				nodeName = Integer.toString(latName);

			// 创建选出的节点的文件夹用来存储数据
			File file = new File(rootDirectory + "pick2\\" + nodeName);
			// 如果文件夹不存在则创建
			if (!file.exists() && !file.isDirectory()) {
				file.mkdirs();// 创建多级目录
			} else {
				System.out.println("//目录存在");
			}

			File file2 = new File(rootDirectory + "xyGPS\\" + nodeName); // 文件初始化
			String[] filelist = file2.list();
			String[] fileName = new String[filelist.length + 1]; // 文件名初始化

			String mNStartDate = "";
			for (int k = 0; k < maxNodesStartDate.length(); k++) {
				if (maxNodesStartDate.charAt(k) != '-') {
					mNStartDate += maxNodesStartDate.charAt(k);
				}
			}

			fileName = FileRead.readfile(rootDirectory + "xyGPS\\" + nodeName); // 获取所有文件的名字存入字符串数组
			String targetDirectory = rootDirectory + "pick2\\" + nodeName + "\\";
			System.out.println(targetDirectory);
			for (int i = 0; i < fileName.length; i++) {
				if (mNStartDate.equals(fileName[i].substring(0, 8))) {
					File[] fileArray = (new File(rootDirectory + "xyGPS\\" + nodeName))
							.listFiles();
					for (int index = i; index < i + givenDays; index++) {
						if (fileArray[index].isFile()) {
							// 复制文件
							copyFile(fileArray[index], new File(targetDirectory
									+ fileArray[index].getName()));
						}
					}

				}
			}

		}
	}

	/**
	 * 找出选出的节点
	 * 
	 * @param listk
	 * @param listj
	 * @param mostFrequency
	 * @param maxNodes
	 * @return
	 */
	private static int[] listKgetPickedNodes(List<Integer> listk,
			List<Integer> listj, int mostFrequency, int maxNodes) {
		int[] pickedNodes = new int[maxNodes];
		int index = 0;
		int count = 0;
		for (Integer temp : listk) {
			// System.out.println(temp + ": " + Collections.frequency(listk,
			// temp));
			if (listk.get(index) == mostFrequency) {
				pickedNodes[count] = listj.get(index);
				count++;
			}
			index++;
		}

		return pickedNodes;
		// TODO Auto-generated method stub

	}

	/**
	 * 计算给定节点数，同时在线时间最长的天数
	 * 
	 * @param pickedNodes2
	 * @throws IOException
	 * @throws ParseException
	 */

	private static void longestDays(String pickedNodes2) throws IOException,
			ParseException {
		// TODO Auto-generated method stub
		System.out.println(caculateZuhe(allNodes, givenNodes)+"种组合");
		int[] picked = new int[givenNodes * caculateZuhe(allNodes, givenNodes)];
		int count = 0;
		for (int j = 0; j < pickedNodes2.length(); j++) {
			if (pickedNodes2.charAt(j) != ' ') {
				picked[count] = Integer.parseInt(pickedNodes2.charAt(j) + "");
				count++;
			}
		}
		int countOne = 0;
		int countGroup = 0;
		int max = 0;
		int[][] onLineMatrix = readFileMatrix(dAYS);
		String days = "";
		String zuheN = "";
		List<Integer> listk = new ArrayList<Integer>();
		List<Integer> listm = new ArrayList<Integer>();
		int c = 0;
		for (int i = 0; i < picked.length; i += givenNodes) {
			for (int m = 0; m < dAYS; m++) {
				for (int k = i; k < i + givenNodes; k++) {
					if (onLineMatrix[picked[k] - 1][m] == 1) {
						countOne++;
					} else {
						countOne = 0;
						countGroup = 0;
						break;
					}
					if (countOne == givenNodes && k == i + givenNodes - 1) {
						listk.add(k);
						listm.add(m);
						c++;
						countGroup++;
						if (max < countGroup) {
							max = countGroup;
						}
						countOne = 0;
					}
				}
			}
			countOne = 0;
			countGroup = 0;
		}
		System.out.println("最长天数为" + max);
		Iterator<Integer> itk = listk.iterator();
		Iterator<Integer> itm = listm.iterator();

		// 得到有最长天数的选出的节点放入longetDysPickedNodes
		int mostFrequency = countFrequency(listk);

		int start = mostFrequency - givenNodes + 1;
		int[] longestDaysPickedNodes = new int[givenNodes];
		int countL = 0;
		for (int k = start; k < start + givenNodes; k++) {
			longestDaysPickedNodes[countL] = picked[k];
			countL++;
		}

		int subtractDays = getSubtractDays(mostFrequency, listk, listm);
		if (subtractDays < 0) {
			System.out.println("统计出错");
		}

		System.out.println("请输入开始日期，格式为yyyy-MM-dd");
		Scanner input = new Scanner(System.in);
		String in = input.nextLine();
		in = in + " 00:00:00";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date startDate = new Date();
		startDate = format.parse(in);
		long startTime = startDate.getTime();
		long longestDaysStartTime = startTime + subtractDays * 24 * 3600 * 1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String time = sdf.format(new Date((longestDaysStartTime)));
		String longestDaysStartDate = "";
		int index = 0;
		while (time.charAt(index) != ' ') {
			longestDaysStartDate += time.charAt(index);
			index++;
		}
		takeOutFiles(longestDaysStartDate, max, longestDaysPickedNodes);

	}

	/**
	 * 把得到的数据写入文件
	 * 
	 * @param longestDaysStartDate
	 * @param max
	 * @param longestDaysPickedNodes
	 * @throws IOException
	 */

	private static void takeOutFiles(String longestDaysStartDate, int max,
			int[] longestDaysPickedNodes) throws IOException {
		// TODO Auto-generated method stub
		int latName;
		String nodeName;
		for (int m = 0; m < longestDaysPickedNodes.length; m++) {
			latName = longestDaysPickedNodes[m] - 1;
			if (latName / 10 == 0)
				nodeName = "00" + Integer.toString(latName);
			else if (latName / 100 == 0)
				nodeName = "0" + Integer.toString(latName);
			else
				nodeName = Integer.toString(latName);

			// 创建选出的节点的文件夹用来存储数据
			File file = new File(rootDirectory + "pick\\" + nodeName);
			// 如果文件夹不存在则创建
			if (!file.exists() && !file.isDirectory()) {
				file.mkdirs();// 创建多级目录
			} else {
				System.out.println("//目录存在");
			}

			File file2 = new File(rootDirectory + "xyGPS\\" + nodeName); // 文件初始化
			String[] filelist = file2.list();
			String[] fileName = new String[filelist.length + 1]; // 文件名初始化

			String lDStartDate = "";
			for (int k = 0; k < longestDaysStartDate.length(); k++) {
				if (longestDaysStartDate.charAt(k) != '-') {
					lDStartDate += longestDaysStartDate.charAt(k);
				}
			}

			fileName = FileRead.readfile(rootDirectory + "xyGPS\\" + nodeName); // 获取所有文件的名字存入字符串数组
			String targetDirectory = rootDirectory + "pick\\" + nodeName + "\\";
			System.out.println(targetDirectory);
			for (int i = 0; i < fileName.length; i++) {
				if (lDStartDate.equals(fileName[i].substring(0, 8))) {
					File[] fileArray = (new File(rootDirectory + "xyGPS\\" + nodeName))
							.listFiles();
					for (int index = i; index < i + max; index++) {
						if (fileArray[index].isFile()) {
							// 复制文件
							copyFile(fileArray[index], new File(targetDirectory
									+ fileArray[index].getName()));
						}
					}

				}
			}

		}
	}

	// 复制文件
	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	/*
	 * 计算开始时间与要写入新文件的时间相差的天数
	 */
	private static int getSubtractDays(int mostFrequency, List<Integer> listk,
			List<Integer> listm) {
		int index = 0;
		for (Integer temp : listk) {
			// System.out.println(temp + ": " + Collections.frequency(listk,
			// temp));
			if (listk.get(index) == mostFrequency) {
				return listm.get(index);
			}
			index++;
		}
		return index - 1;

		// TODO Auto-generated method stub

	}

	/**
	 * 计算连续的值的个数
	 * 
	 * @param listk
	 * @return
	 */
	private static int countFrequency(List<Integer> listk) {
		// TODO Auto-generated method stub
		int maxF = 0;
		int pickednodes = 0;
		for (Integer temp : listk) {
			// System.out.println(temp + ": " + Collections.frequency(listk,
			// temp));
			maxF = maxF > Collections.frequency(listk, temp) ? maxF
					: Collections.frequency(listk, temp);

		}
		for (Integer temp : listk) {
			if (Collections.frequency(listk, temp) == maxF) {
				pickednodes = temp;
			}

		}
		return pickednodes;
	}

	/**
	 * 计算组合的所有情况总数
	 * 
	 * @param allNodes2
	 * @param givenNodes2
	 * @return
	 */

	private static int caculateZuhe(int allNodes2, int givenNodes2) {
		// TODO Auto-generated method stub
		int a = allNodes2;
		int b = givenNodes2;
		if (b == 1) {
			return b;
		}
		for (int i = a - 1; i > allNodes2 - givenNodes2; i--) {
			a = a * i;
		}
		for (int i = b - 1; i > 1; i--) {
			b = b * i;
		}
		return a / b;
	}

	static void Zuhe(int allN, int givenN) {

		for (int i = 0; i < allNodes; i++) {
			all[i] = i + 1;
		}
		int i, j;
		int count = 0;

		for (i = givenN; i <= allN; i++) {
			given[givenN - 1] = i - 1;
			if (givenN > 1)
				Zuhe(i - 1, givenN - 1);
			else {
				for (j = 0; j <= givenNodes - 1; j++) {
					pickedNodes += all[given[j]] + " ";
				}
			}
		}
	}

	/**
	 * 读取data.txt中的二维01数组
	 * 
	 * @param dAYS
	 * @param onLinePerson
	 * @param dAYS
	 * @param nodeNum
	 * @param startTime
	 * @return
	 * @throws IOException
	 */
	private static int[][] readFileMatrix(int dAYS) throws IOException {
		// TODO Auto-generated method stub
		File file = new File(rootDirectory + "onLineMatrix\\data.txt"); // 文件初始化
		int totalDataLines = FileRead
				.getTotalLines(rootDirectory + "onLineMatrix\\data.txt");
		int[][] onLineDays = new int[totalDataLines][dAYS];
		int count = 0;
		String line = "";
		for (int i = 1; i <= totalDataLines; i++) {
			count = 0;
			line = FileRead.readLineVarFile(rootDirectory + "onLineMatrix\\data.txt", i);
			for (int j = 0; j < line.length(); j++) {
				if (line.charAt(j) != ' ') {
					onLineDays[i - 1][count] = Integer.parseInt(line.charAt(j)
							+ "");
					count++;
				}
			}
		}
		return onLineDays;

	}

}
