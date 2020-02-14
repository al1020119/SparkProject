# Spark项目实战

spark-project里面放的是spark处理慕课网日志
web项目是将处理的数据使用Echarts进行可视化展示

### 一、项目梳理

网课日志记录用户行为：用户每次访问网站时所有的行为数据（访问、浏览、搜索、点击...）用户行为轨迹、流量日志，根据这些行为，对用户进行推荐，在后台对用户进行分析，并为用户贴上标签。用户行为日志生成的渠道来自，web服务器（Nginx）或者Ajax。

- 日志数据内容：

1）访问的系统属性： 操作系统、浏览器等等

2）访问特征：点击的url、从哪个url跳转过来的(referer)、页面上的停留时间等

3）访问信息：session_id、访问ip(访问城市)，访问时间和区域

例如：2013-05-19 13:00:00     http://www.taobao.com/17/?tracker_u=1624169&type=1      B58W48U4WKZCJ5D1T3Z9ZY88RU7QA7B1        http://hao.360.cn/      1.196.34.243   

- 数据处理流程

1）数据采集

Flume：web日志写入到HDFS

2）数据清洗ETL

脏数据Spark、Hive、MapReduce 或者是其他的一些分布式计算框架  清洗完之后的数据可以存放在HDFS(Hive/Spark SQL的表)

3）数据处理

按照我们的需要进行相应业务的统计和分析 Spark、Hive、MapReduce 或者是其他的一些分布式计算框架

4）处理结果入库

结果可以存放到RDBMS、NoSQL

5）数据的可视化

通过图形化展示的方式展现出来：饼图、柱状图、地图、折线图  ECharts、HUE、Zeppelin（后两个一般是大数据开发人员使用的）

**离线数据处理架构，本次实战实现的是右侧的流程**
<div align="center"> <img  src="/pictures/Data-processing-architecture.png"/> </div>

- 项目需求

需求一：统计慕课网主站最受欢迎的课程/手记的Top N访问次数

需求二：根据地市统计慕课网主站最受欢迎的Top N课程，根据IP地址提取出城市信息

需求三：按流量统计慕课网主站最受欢迎的Top N课程

- 慕课网主站日志介绍

文件有些大，压缩后500MB，解压有5G，GitHub有上传限制，因此无法上传。这里抽取出一万行用来测试。

对日志执行 head -10000 access.20161111.log >> 10000_access.log，抽取后的文件只有2.6MB，或者使用access.log文件。

一般的日志处理方式，我们是需要进行分区的，按照日志中的访问时间进行相应的分区，比如：d,h,m5(每5分钟一个分区)数据处理流程，本次项目按照天进行分区

- 日志清洗：

输入：访问时间、访问URL、耗费的流量、访问IP地址信息
输出：URL、cmsType(video/article)、cmsId(编号)、流量、ip、城市信息、访问时间、天

- ip解析包的下载

使用github上已有的开源项目

1）git clone https://github.com/wzhe06/ipdatabase.git

2）编译下载的项目：mvn clean package -DskipTests 会出现一个target目录，里面有一个jar包

3）安装jar包到自己的maven仓库

mvn install:install-file -Dfile=/home/willhope/sparkdata/ipdatabase/target/ipdatabase-1.0-SNAPSHOT.jar -DgroupId=com.ggstar -DartifactId=ipdatabase -Dversion=1.0 -Dpackaging=jar

4）在IDEA中使用，在项目的pom.xml中进行引用

```xml

    <dependency>
      <groupId>com.ggstar</groupId>
      <artifactId>ipdatabase</artifactId>
      <version>1.0</version>
    </dependency>

    <dependency>
      <groupId>org.apache.poi</groupId>
      <artifactId>poi-ooxml</artifactId>
      <version>3.14</version>
    </dependency>
    
    <dependency>
      <groupId>org.apache.poi</groupId>
      <artifactId>poi</artifactId>
      <version>3.14</version>
    </dependency>

    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>5.1.38</version>
    </dependency>

```