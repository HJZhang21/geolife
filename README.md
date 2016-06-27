流程

- 读取原始数据文件（E:/GPS）并转换坐标、海拔、时间等，存入新的文件夹中（E:/xyGPS）
- 在xyGPS文件夹中，把所有一个数据文件里包含两天的数据分开为两个文件。再把所有包含同一天数据的文件合并成一个数据文件。
- 统计各个节点的在线天数，把节点名和其在线与否（在线为1，不在线为0）存入一个二维数组，存入E:/onLineMatrix/data.txt里。
- 给定节点数，计算最长在线持续天数。给定在线持续天数，求最多节点数。最后把求得的信息（节点及其对应天的数据文件）分别放入e:/pick和e:/pick2文件夹中。

开发环境

- java 
- Eclipse

功能

- 给定节点数，计算最长在线持续天数。给定在线持续天数，求最多节点数。并把数据统计到文件中。
文件说明（各个类的详细方法说明见代码中注释。）

GPS.java

- 包含一个主函数和一个坐标转换总方法Transition()。主函数包括了坐标转换，分割合并数据文件和统计数据来实现以上功能的方法调用。
- 坐标转换详细方法为LatLonToUTMXY(double lat, double lon, UTMCoor xy)  //转换经纬度到XY坐标MapLatLonToXY(double phi, double lambda,double lambda0, UTMCoor xy)
- ArcLengthOfMeridian(double phi)
- UTMCentralMeridian(int zone)
- DegreeToRadian(double deg)  //弧度转换
- getZoneByLongitude(double lon)  //用经度来得到区
Split.java
- 逐个节点读取文件，找到包含有两天数据的文件，把它们分割为两个文件，并把新的文件命名为分割后的第一个数据日期。

Merge.java

- 逐个节点读取文件名，找到包含相同日期的文件，把第二个文件数据拼接到第一个文件中，然后删除第二个文件。

Statistic.java

- 统计每个节点在给定开始日期和统计天数后在线与否。按照在线为1，不在线为0，以天为单位，统计到一个二维矩阵中，并存入文件data.txt。

Caculate.java

- 实现了两个功能，由用户选择执行。第一个为给定节点数，求最长持续在线天数:先利用排列组合，Zuhe(int allN, int givenN)方法递归求得有给定节点个数的节点组合存入数组。再依次取出所有情况的节点逐个计算持续的天数，在longestDays(String pickedNodes2)方法中把最长天数存入变量max中。方法同时还要记录是哪些节点从哪天开始有最长持续天数，以便最后用takeOutFiles(String longestDaysStartDate, int max,int[] longestDaysPickedNodes)方法把统计出来的结果存入新的文件夹pick中。第二个为给定持续天数，求同时在线的最多节点。首先用户输入一个持续的天数T，然后在getMostNodes(int givenDays2)中求得同时包含连续T天在线的节点数，方法同时还要记录是哪几个节点从哪天开始符合给定持续天数T这个条件的。最后在takeOutFiles2(int[] pickedNodes2, int maxNodes,String maxNodesStartDate)中把统计出来的结果存入新的文件夹pick2中。

FileRead.java

- 包括了一些读取文件的方法，如一行一行读取文件readLineVarFile(String fileName, int lineNumber)
- 统计文件行数 getTotalLines(String fileName)
- 读取某文件夹下所有文件readfile(String filepath)

注意事项

- 在使用本程序前，首先要在E盘目录下创建三个文件夹，分别命名为GPS，xyGPS,onLineMatrix。GPS中放入所有节点的数据文件，一个节点用一个文件夹保存，命名为：000，001，002...xyGPS中在运行程序后会自动生成转换坐标后的各节点的数据文件，在onLineMatrix中创建data.txt文件，在执行到Statistic.java中的方法时data.txt文件用来存储节点在线与否的二维01矩阵。
- 其次，要在执行前在GPS.java里面设置nodeNum和dAYS的值,存入GPS文件夹中的节点有多少个，就把nodeNum设置为多少。dAYS表明了用户想要统计多少天的数据，可以任意设置。
