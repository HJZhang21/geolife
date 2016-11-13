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
	private String inputPath = "GPS\\";  // 读取原始GPS数据文件的路径；000为第一个节点，001为第二个节点...
//	private String tracePath = "Trajectory\\";  // rootDirectory\\inputPath\\$NodeID$\\tracePath\\
	private String tracePath = "";  // rootDirectory\\inputPath\\$NodeID$\\tracePath\\
	private String xyPath = "xyGPS\\";
	private String matrixPath = "onlineMatrix\\";
	
//	// 完整参数
//	static int countNode = 182;// 总节点个数
//	static int countDay = 30;// 统计的天数
	
	// 测试参数
	static int countNode = 5;// 总节点个数
	static int countDay = 10;// 统计的天数
	
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
