package test;

import java.util.Date;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class Test {
	
	private static void certainTgetN(boolean[][] onLinePerson, int dAYS,
			int nodeNum, long startTime) {
		// TODO Auto-generated method stub
		System.out.println("请输入您想要包含的最小连续天数");
		Scanner input = new Scanner(System.in);
		int minD=input.nextInt();
		if(minD>dAYS){
			System.out.println("超过最大限制");
			return;
		}
		int countOne=0;int countGroup=0;
		int max=0;
		for(int c=0;c<dAYS-(minD-1);c++){
			for(int j=0;j<nodeNum;j++){
				for(int i=c;i<c+minD;i++){
					if(onLinePerson[j][i]==true){
						countOne++;
					}
					else{
						countGroup=0;
						countOne=0;
						break;
					}
					if(i==c+minD-1&&countOne==minD)
					{
						countGroup++;
						if(max<countGroup)
							max=countGroup;
						countOne=0;
					}
				}
			}
			countGroup=0;
			countOne=0;
		}
		System.out.println("最大节点数为"+max);
		
	}

	private static void certainNgetT(boolean[][] onLinePerson, int dAYS,
			int nodeNum, long startTime) {
		// TODO Auto-generated method stub
		System.out.println("请输入您想要包含的最小节点数");
		Scanner input = new Scanner(System.in);
		int minN=input.nextInt();
		if(minN>nodeNum){
			System.out.println("超过最大限制");
			return;
		}
		int countOne=0;int countGroup=0;
		int max=0;
		int[] start=new int[100];
		int n=0;
		for(int c=0;c<nodeNum-(minN-1);c++){
			for(int j=0;j<dAYS;j++){
				for(int i=c;i<c+minN;i++){
					if(onLinePerson[i][j]==true){
						countOne++;
					}
					else{
						countGroup=0;
						countOne=0;
						break;
					}
					if(i==c+minN-1&&countOne==minN)
					{
						if(countGroup==0){
							start[n]=j;
							System.out.println(j);
						}
						countGroup++;
						if(max<countGroup)
							max=countGroup;
						countOne=0;
					}
				}
			}
			countGroup=0;
			countOne=0;
		}
		
		
		
		System.out.println("最长天数为"+max);
		
	}

	private static void countNodes(boolean[][] onLinePerson, int days,
			int nodeNum, long startTime) {
		int day = 0;
		String in;
		// TODO Auto-generated method stub
		do {
			System.out.println("请输入要查询哪一天");
			Scanner input = new Scanner(System.in);
			in = input.nextLine();
			in = in + " 00:00:00";
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			Date checkDate = new Date();
			try {
				checkDate = format.parse(in);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				System.out.println("输入有误");
			}
			long checkTime = checkDate.getTime();
			int subtractDays = (int) ((checkTime - startTime) / 1000 / 3600 / 24);

			int count = 0;
			for (int i = 0; i < nodeNum; i++) {
				if (onLinePerson[i][subtractDays])
					count++;
			}
			System.out.println("有" + count + "个节点在这一天在线");

		} while (in != null);
	}

}