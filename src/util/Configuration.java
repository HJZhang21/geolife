/**
 * 
 */
package util;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Yun-Miao SUN
 *
 */
public final class Configuration {

	static Configuration singleInstance;
	private String rootDirectory = "E:\\";
	private String inputPath = "GPS\\";  // ��ȡԭʼGPS�����ļ���·����000Ϊ��һ���ڵ㣬001Ϊ�ڶ����ڵ�...
//	private String tracePath = "Trajectory\\";  // rootDirectory\\inputPath\\$NodeID$\\tracePath\\
	private String tracePath = "";  // rootDirectory\\inputPath\\$NodeID$\\tracePath\\
	private String xyPath = "xyGPS\\";
	private String matrixPath = "onlineMatrix\\";
	
//	// ��������
//	static int countNode = 182;// �ܽڵ����
//	static int countDay = 30;// ͳ�Ƶ�����
	
	// ���Բ���
	static int countNode = 5;// �ܽڵ����
	static int countDay = 10;// ͳ�Ƶ�����
	
	public Configuration() {
		
	}
	
	static public Configuration getConfiguration() {
		if( singleInstance == null )
			singleInstance = new Configuration();
		return singleInstance;	
	}
	
	public boolean setRootDirectory(String rootDirectory) throws FileNotFoundException {
	    File file = new File(rootDirectory);    
	    if( file.isDirectory() )
	    {
	    	this.rootDirectory = rootDirectory;
	    }
	    else
	    {
	    	throw new FileNotFoundException();
	    }
		return false;
	}

	public String getRootDirectory() {
		createPathIfNotExist(rootDirectory);
		return rootDirectory;
	}

	public String getInputDirectory() {
		createPathIfNotExist(rootDirectory + inputPath);
		return rootDirectory + inputPath;
	}

	public String getTracePath() {
		return tracePath;
	}

	public String getXYDirectory() {
		createPathIfNotExist(rootDirectory + xyPath);
		return rootDirectory + xyPath;
	}
	
	public String getMatrixDirectory() {
		createPathIfNotExist(rootDirectory + matrixPath);
		return rootDirectory + matrixPath;
	}
	
	public int getCountNode() {
		return countNode;
	}
	
	public int getCountDay() {
		return countDay;
	}
	
	private boolean createPathIfNotExist(String path) {
	    File file = new File(path);    
	    if( file.isFile() 
	    	|| file.isDirectory() )
	    {
	    	return true;
	    }
	    else
	    {
	    	file.mkdir();
			return false;
	    }
	}
}
