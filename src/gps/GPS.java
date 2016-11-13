package gps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.FileRead;
import sort.Merge;
import sort.Split;
import statistic.Caculate;
import statistic.Statistic;
import util.Configuration;

public class GPS {

//	static String rootDirectory = "E:\\SUN\\WORKSPACE\\DATASET\\Geolife\\";
//	public static final String prePath = rootDirectory + "GPS\\";// 此处为读取原始GPS数据文件的地方
//													// 000为第一个节点，001为第二个节点...

	static double sm_a = 6378137.0;
	static double sm_b = 6356752.314;
	double sm_EccSquared = 6.69437999013e-03;
	static double UTMScaleFactor = 0.9996;

//	static int nodeNum = 182;// 总节点个数
//	static int dAYS = 30;// 统计的天数
	
	public static void main(String[] args) throws IOException, ParseException {

		Transition();//坐标时间海拔转换

		Split.splitFile( Configuration.getConfiguration().getCountNode() );//跨天文件分割
		
		for (int i = 0; i < 5; i++) {
			Merge.mergeFile( Configuration.getConfiguration().getCountNode() );//同天文件合并
		}
		Statistic.statisticFile( Configuration.getConfiguration().getCountNode() 
				,  Configuration.getConfiguration().getCountDay() );//统计在线与否存入二维数组和文件
		
		Caculate caculate = new Caculate( Configuration.getConfiguration().getCountNode() 
				,  Configuration.getConfiguration().getCountDay() );//读取文件实现指定功能
		caculate.caculateData();
	}

	/**
	 * 读取原始文件并转换坐标，输出到新目录中
	 */
	private static void Transition() {
		// TODO Auto-generated method stub
		int initialZone; // 计算一天中第一个数据的标准zone，此后数据的坐标系以它为准
		int lineNumber = 7; // 所有数据文件的数据都是从第七行开始的
		int totalDataLines = 0; // 定义一个数据文件的总行数

		String latPathStr = "";
		int latPath;
		
		System.out.println("[经纬度-横纵坐标转换]");
		
		for (int m = 0; m < Configuration.getConfiguration().getCountNode(); m++) {// 利用for循环选出GPS目录下各个子目录：000、001、002...
			System.out.println("节点" + m + "：");
			latPath = m;
			if (latPath / 10 == 0)
				latPathStr = "00" + Integer.toString(latPath);
			else if (latPath / 100 == 0)
				latPathStr = "0" + Integer.toString(latPath);
			else
				latPathStr = Integer.toString(latPath);

			String path = Configuration.getConfiguration().getInputDirectory() + latPathStr + Configuration.getConfiguration().getTracePath();// 每次循环的最终路径

			File file = new File(path); // 文件初始化
			String[] filelist = file.list();
			String[] fileName = new String[filelist.length + 1]; // 文件名初始化
			try {
				fileName = FileRead.readfile(path); // 获取一个节点所有文件的名字存入字符串数组
			} catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			/* 对每个文件进行经纬度转坐标操作 */
			for (int c = 0; c < fileName.length; c++) {
				try {
					totalDataLines = FileRead.getTotalLines(path + "\\"
							+ fileName[c]) - 7;// 获取一个数据文件的总行数
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String[] latStr = new String[totalDataLines + 1];// 初始化存取经纬度，日期和海拔的数组
				String[] lonStr = new String[totalDataLines + 1];
				String[] date = new String[totalDataLines + 1];
				String[] alt = new String[totalDataLines + 1];

				for (int i = 0; i < latStr.length; i++) {
					latStr[i] = "";
					lonStr[i] = "";
					date[i] = "";
					alt[i] = "";
				}

				float[] lat = new float[totalDataLines + 1];// 转化成浮点型
				float[] lon = new float[totalDataLines + 1];

				/* 利用charAt方法截取每行数据的各个属性分别存入各自数组 */
				try {
					String[] dataForOneFile = new String[totalDataLines + 1];
					for (int i = 0; i < dataForOneFile.length; i++) {
						dataForOneFile[i] = FileRead.readLineVarFile(path + "\\"
								+ fileName[c], lineNumber);// 读取某一数据文件指定行
						int n, j;
						int count = 0;
						for (n = 0; dataForOneFile[i].charAt(n) != ','; n++) {// 获取经纬度和海拔信息
							latStr[i] += dataForOneFile[i].charAt(n);
						}
						for (j = n + 1; dataForOneFile[i].charAt(j) != ','; j++) {
							lonStr[i] += dataForOneFile[i].charAt(j);
						}
						for (int l = j + 1; l < dataForOneFile[i].length() - 1; l++) {
							if (dataForOneFile[i].charAt(l) == ',')
								count++;
							if (count == 1) {
								if (dataForOneFile[i].charAt(l + 1) != ',')
									alt[i] += dataForOneFile[i].charAt(l + 1);
							}
							if (count >= 3) {
								if (dataForOneFile[i].charAt(l + 1) == ',') {
									date[i] += " ";
								} else {
									date[i] += dataForOneFile[i].charAt(l + 1);
								}
							}
						}
						lat[i] = Float.parseFloat(latStr[i]);
						lon[i] = Float.parseFloat(lonStr[i]);
						lineNumber++;
					}

					initialZone = getZoneByLongitude(lon[0]);// 获取标准zone

					String[] dateChanged = new String[date.length];
					dateChanged = changeDate(date);// 转换时间为北京时间

					int[] second = new int[dataForOneFile.length];// 初始化第一个时间之后经过的秒数
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					try {

						second[0] = 0;
						Date beginTime = sdf.parse(dateChanged[0]);
						for (int i = 0; i < dataForOneFile.length - 1; i++) {
							Date endTime = sdf.parse(dateChanged[i + 1]);
							// 默认为毫秒，除以1000是为了转换成秒
							second[i + 1] = (int) ((endTime.getTime() - beginTime
									.getTime()) / 1000);// 秒
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// 把文件名改为每个文件数据第一行的北京时间
					dateChanged[0] = dateChanged[0].trim();
					String newfileName = "";// 新文件名
					if (dateChanged[0] != null && !"".equals(dateChanged[0])) {
						for (int i = 0; i < dateChanged[0].length(); i++) {
							if (dateChanged[0].charAt(i) >= 48
									&& dateChanged[0].charAt(i) <= 57) {
								newfileName += dateChanged[0].charAt(i);
							}
						}

					}
					newfileName += ".plt";

					GPSTransition(lat, lon, initialZone, dateChanged, second,
							alt, newfileName, latPathStr);// 进行经纬度转换和文件写入操作

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				lineNumber = 7;// 重新把文件行数定位到第七行，准备下一个文件的读取
				System.out.print("..." + (c + 1) + "");// 打印转换完毕的文件个数
				if (c == fileName.length - 1) {
					System.out.println("\n共" + (c + 1) + "个文件转换完毕。");
				}
			}
		}
	}

	/**
	 * 转换格林尼治时间到北京时间
	 * 
	 * @param date
	 * @return
	 */
	private static String[] changeDate(String[] date) {
		// TODO Auto-generated method stub
		String[] newDate = new String[date.length];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < date.length; i++) {
			Date sec;
			long seconds = 0;
			try {
				sec = sdf.parse(date[i]);
				seconds = sec.getTime() + 28800000;
				newDate[i] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date(seconds));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return newDate;
	}

	/* 经纬度转换成XY坐标函数 */
	private static void GPSTransition(float[] lat, float[] lon, int zone,
			String[] dateChanged, int[] second, String[] alt, String fileName,
			String latPath) {
		// TODO Auto-generated method stub
		for (int i = 0; i < lat.length; i++) {
			WGS84Coor inputWGS84 = new WGS84Coor(lat[i], lon[i]);
			UTMCoor outputUTM = new UTMCoor();

			LatLonToUTMXY(inputWGS84.lat, inputWGS84.lon, zone, outputUTM);
			try {
				String folderPath = Configuration.getConfiguration().getXYDirectory() + latPath;
				File folderFile = new File(folderPath);
				if (!folderFile.exists() && !folderFile.isDirectory()) {
					folderFile.mkdir();
				}
				String filePath = Configuration.getConfiguration().getXYDirectory() + latPath + "\\" + fileName;
				File writeFile = new File(filePath);
				BufferedWriter out = new BufferedWriter(new FileWriter(
						writeFile, true));
				float mAlt = (float) (Float.parseFloat(alt[i]) * 0.305);// 将海拔由英尺转换成米
				out.write(dateChanged[i] + " " + second[i] + " " + outputUTM.x
						+ " " + outputUTM.y + " " + mAlt + " " + zone);
				out.newLine();

				out.close();
				out = null;
				writeFile = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void testGPS(double lat, double lon) {
		// TODO Auto-generated method stub
		WGS84Coor inputWGS84 = new WGS84Coor(lat, lon);
		UTMCoor outputUTM = new UTMCoor();
		int outputZone = 0;
		outputZone = LatLonToUTMXY(inputWGS84.lat, inputWGS84.lon, outputUTM);
		// System.out.println(inputWGS84.lat+" "+inputWGS84.lon);
		System.out.println(outputUTM.x + " " + outputUTM.y + " " + outputZone);
	}

	private static int LatLonToUTMXY(double lat, double lon, UTMCoor xy) {
		// TODO Auto-generated method stub
		int zone = getZoneByLongitude(lon);
		LatLonToUTMXY(lat, lon, zone, xy);
		return zone;
	}

	private static void LatLonToUTMXY(double lat, double lon, int zone,
			UTMCoor xy) {
		// TODO Auto-generated method stub
		// 原代码函数输入为radian，现改为degree
		lat = DegreeToRadian(lat);
		lon = DegreeToRadian(lon);

		MapLatLonToXY(lat, lon, (Double) UTMCentralMeridian(zone), xy);

		/* Adjust easting and northing for UTM system. */
		xy.x = xy.x * UTMScaleFactor + 500000.0;
		xy.y = xy.y * UTMScaleFactor;
		if (xy.y < 0.0)
			xy.y += 10000000.0;
	}

	private static void MapLatLonToXY(double phi, double lambda,
			double lambda0, UTMCoor xy) {
		// TODO Auto-generated method stub
		double N, nu2, ep2, t, t2, l;
		double l3coef, l4coef, l5coef, l6coef, l7coef, l8coef;
		double tmp;

		/* Precalculate ep2 */
		ep2 = (Math.pow(sm_a, 2.0) - Math.pow(sm_b, 2.0)) / Math.pow(sm_b, 2.0);

		/* Precalculate nu2 */
		nu2 = ep2 * Math.pow(Math.cos(phi), 2.0);

		/* Precalculate N */
		N = Math.pow(sm_a, 2.0) / (sm_b * Math.sqrt(1 + nu2));

		/* Precalculate t */
		t = Math.tan(phi);
		t2 = t * t;
		tmp = (t2 * t2 * t2) - Math.pow(t, 6.0);

		/* Precalculate l */
		l = lambda - lambda0;

		/*
		 * Precalculate coefficients for l**n in the equations below so a normal
		 * human being can read the expressions for easting and northing -- l**1
		 * and l**2 have coefficients of 1.0
		 */
		l3coef = 1.0 - t2 + nu2;

		l4coef = 5.0 - t2 + 9 * nu2 + 4.0 * (nu2 * nu2);

		l5coef = 5.0 - 18.0 * t2 + (t2 * t2) + 14.0 * nu2 - 58.0 * t2 * nu2;

		l6coef = 61.0 - 58.0 * t2 + (t2 * t2) + 270.0 * nu2 - 330.0 * t2 * nu2;

		l7coef = 61.0 - 479.0 * t2 + 179.0 * (t2 * t2) - (t2 * t2 * t2);

		l8coef = 1385.0 - 3111.0 * t2 + 543.0 * (t2 * t2) - (t2 * t2 * t2);

		/* Calculate easting (x) */
		xy.x = N
				* Math.cos(phi)
				* l
				+ (N / 6.0 * Math.pow(Math.cos(phi), 3.0) * l3coef * Math.pow(
						l, 3.0))
				+ (N / 120.0 * Math.pow(Math.cos(phi), 5.0) * l5coef * Math
						.pow(l, 5.0))
				+ (N / 5040.0 * Math.pow(Math.cos(phi), 7.0) * l7coef * Math
						.pow(l, 7.0));

		/* Calculate northing (y) */
		xy.y = ArcLengthOfMeridian(phi)
				+ (t / 2.0 * N * Math.pow(Math.cos(phi), 2.0) * Math
						.pow(l, 2.0))
				+ (t / 24.0 * N * Math.pow(Math.cos(phi), 4.0) * l4coef * Math
						.pow(l, 4.0))
				+ (t / 720.0 * N * Math.pow(Math.cos(phi), 6.0) * l6coef * Math
						.pow(l, 6.0))
				+ (t / 40320.0 * N * Math.pow(Math.cos(phi), 8.0) * l8coef * Math
						.pow(l, 8.0));
	}

	private static double ArcLengthOfMeridian(double phi) {
		// TODO Auto-generated method stub
		double alpha, beta, gamma, delta, epsilon, n;
		double result;

		/* Precalculate n */
		n = (sm_a - sm_b) / (sm_a + sm_b);

		/* Precalculate alpha */
		alpha = ((sm_a + sm_b) / 2.0)
				* (1.0 + (Math.pow(n, 2.0) / 4.0) + (Math.pow(n, 4.0) / 64.0));

		/* Precalculate beta */
		beta = (-3.0 * n / 2.0) + (9.0 * Math.pow(n, 3.0) / 16.0)
				+ (-3.0 * Math.pow(n, 5.0) / 32.0);

		/* Precalculate gamma */
		gamma = (15.0 * Math.pow(n, 2.0) / 16.0)
				+ (-15.0 * Math.pow(n, 4.0) / 32.0);

		/* Precalculate delta */
		delta = (-35.0 * Math.pow(n, 3.0) / 48.0)
				+ (105.0 * Math.pow(n, 5.0) / 256.0);

		/* Precalculate epsilon */
		epsilon = (315.0 * Math.pow(n, 4.0) / 512.0);

		/* Now calculate the sum of the series and return */
		result = alpha
				* (phi + (beta * Math.sin(2.0 * phi))
						+ (gamma * Math.sin(4.0 * phi))
						+ (delta * Math.sin(6.0 * phi)) + (epsilon * Math
						.sin(8.0 * phi)));

		return result;
	}

	private static Object UTMCentralMeridian(int zone) {
		// TODO Auto-generated method stub
		return DegreeToRadian(-183.0 + (zone * 6.0));
	}

	private static double DegreeToRadian(double deg) {
		// TODO Auto-generated method stub
		return (deg / 180.0 * Math.PI);
	}

	private static int getZoneByLongitude(double lon) {
		// TODO Auto-generated method stub
		int longZone = 0;
		if (lon < 0.0) {
			longZone = (int) (((180.0 + lon) / 6) + 1);
		} else {
			longZone = (int) ((lon / 6) + 31);
		}
		return longZone;
	}

}
