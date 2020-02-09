# Spark项目

本项目分为Java版本和Scala版本。在学习的时候使用Scala，因为企业生产中都是使用Java来编写的，因此之后用Java进行重构。

本项目中Spark SQL和Spark Streaming目录下都会有一个Actual-Project和一个learning-project，前者是本阶段学习整体完成后，进行的实战项目，后者是阶段性学习时，日常的测试。

# Spark及生态圈概述

### 一、产生的背景

MapReduce局限性：代码非常繁琐，只能支持map和reduce方法，执行效率低下。map处理完后的数据回写到磁盘上，reduce再从磁盘上把数据拉取过来，因此执行效率低下。map和reduce都会对应一个jvm，因此作业量大，则线程开销非常庞大。不适合迭代多次，交互感很低，不支持流式处理。

在hadoop上框架多样化，每个框架各干各自的事：批处理：MapReduce、Hive、Pig  流式处理：Strom、Jstrom  交互式计算：Impala

这使得学习、运维成本非常高。因此Spark由此而生。

### 二、Spark发展历史
  
1、2009年，Spark诞生于伯克利大学AMPLab，属于伯克利大学的研究性项目；

2、2010 年，通过BSD 许可协议正式对外开源发布；

3、2012年，Spark第一篇论文发布，第一个正式版（Spark 0.6.0）发布；

4、2013年，成为了Aparch基金项目；发布Spark Streaming、Spark Mllib（机器学习）、Shark（Spark on Hadoop）；

5、2014 年，Spark 成为 Apache 的顶级项目； 5 月底 Spark1.0.0 发布；发布 Spark Graphx（图计算）、Spark SQL代替Shark；

6、2015年，推出DataFrame（大数据分析）；2015年至今，Spark在国内IT行业变得愈发火爆，大量的公司开始重点部署或者使用Spark来替代MapReduce、Hive、Storm等传统的大数据计算框架；

7、2016年，推出dataset（更强的数据分析手段）；

8、2017年，structured streaming 发布；

9、2018年，Spark2.4.0发布，成为全球最大的开源项目。

### 三、Spark基本组件

Spark Core；Spark 核心 API，提供 DAG 分布式内存计算框架

Spark SQL：提供交互式查询 API

Spark Streaming：实时流处理

SparkML：机器学习 API

Spark Graphx：图形计算

### 四、Spark和Hadoop的对比

mapreduce 读 – 处理 - 写磁盘 -- 读 - 处理 - 写

spark     读 - 处理 - 写内存　-- 读　-- 处理  --（需要的时候）写磁盘 - 写

Spark 是在借鉴了 MapReduce 之上发展而来的，继承了其分布式并行计算的优点并改进了 MapReduce 明显的缺陷，（spark 与 hadoop 的差异）具体如下：

首先，Spark 把中间数据放到内存中，迭代运算效率高。MapReduce 中计算结果需要落地，保存到磁盘上，这样势必会影响整体速度，而 Spark 支持 DAG 图的分布式并行计算的编程框架，减少了迭代过程中数据的落地，提高了处理效率。（延迟加载）

其次，Spark 容错性高。Spark 引进了弹性分布式数据集 RDD (Resilient DistributedDataset) 的抽象，它是分布在一组节点中的只读对象集合，这些集合是弹性的，如果数据集一部分丢失，则可以根据“血统”（即允许基于数据衍生过程）对它们进行重建。另外在RDD 计算时可以通过 CheckPoint 来实现容错。

最后，Spark 更加通用。mapreduce 只提供了 Map 和 Reduce 两种操作，Spark 提供的数据集操作类型有很多，大致分为：Transformations 和 Actions 两大类。Transformations包括 Map、Filter、FlatMap、Sample、GroupByKey、ReduceByKey、Union、Join、Cogroup、MapValues、Sort 等多种操作类型，同时还提供 Count, Actions 包括 Collect、Reduce、Lookup 和 Save 等操作

总结：Spark 是 MapReduce 的替代方案，而且兼容 HDFS、Hive，可融入 Hadoop 的生态系统，以弥补 MapReduce 的不足。

# Spark准备

### 一、Spark源码的编译

在spark.apache.org中下载spark，这里我们选择2.4.4的source code版本。下载后解压到software文件夹中。[Spark学习的官网](http://spark.apache.org/docs/latest/)。

注意：如果使用maven编译的话，这里有一个非常大的坑，官网要求基于 maven 的构建是 Apache Spark 的引用构建。 使用 Maven 构建 Spark 需要 Maven 3.5.4和 java8。 注意，从 Spark 2.2.0开始，Java 7的支持就被移除了。因此，一定要下载好对应的maven版本，否则会出现编译错误的情况。此外，因为本项目都是在CDH5.15.1平台上，因此在spark源码下的pom.xml文件中，要加上下面。但其实也可以使用Spark自带的maven进行编译，这样会省一些事情，本项目用的是自带的进行编译。

````xml
  <repository>
        <id>cloudera</id>
        <url>https://repository.cloudera.com/artifactory/cloudera-repos</url>
  </repository>
````

在spark源码目录下的dev目录中更改 make-distribution.sh

```bash
export MAVEN_OPTS="${MAVEN_OPTS:--Xmx2g -XX:ReservedCodeCacheSize=512m -XX:ReservedCodeCacheSize=512m}"
```

此时看一下自己的scala版本，在终端中输入scala -version进行查看，spark2.4版本只支持2.11和2.12版本的scala。然后在spark目录下执行./dev/change-scala-version.sh 2.11

在Spark源码目录下，执行下面的语句，这个过程会非常缓慢。估计得30分钟到1个小时，刚开始编译时，可能会发现终端一直卡着不动，这是正在检索所需要的环境，要耐心等待。

```m
./build/mvn -Pyarn -Phadoop-2.6 -Phive -Phive-thriftserver -Dhadoop.version=2.6.0-cdh5.15.1 -DskipTests clean package

推荐使用这个
./dev/make-distribution.sh --name 2.6.0-cdh5.15.1 --tgz  -Pyarn -Phadoop-2.6 -Phive -Phive-thriftserver -Dhadoop.version=2.6.0-cdh5.15.1
执行完后，会出现一个spark-2.4.4-bin-2.6.0-cdh5.15.1.tgz包。
```

### 二、Spark环境的搭建

将编译好的spark-2.4.4-bin-2.6.0-cdh5.15.1.tgz包解压到app中。进入到解压的目录下，我们在spark的bin目录下，看到很多.cmd的文件，这些文件是在windows上运行的，因此我们可以删除，使用rm -rf *.cmd

将spark配入到系统环境变量中，使用pwd查看路径，然后在终端中打开/etc/profile，添加如下的代码：

```xml
  export SPARK_HOME=/home/willhope/app/spark-2.4.4-bin-2.6.0-cdh5.15.1
  export PATH=$SPARK_HOME/bin:$PATH

  然后保存后，执行source etc/profile
```

在spark目录下的bin下，执行spark-shell --master local[2]

可以在 http://192.168.0.100:4040 监控spark。

在conf目录下，设置 cp spark-env.sh.template spark-env.sh，然后配置下面的

```bash
spark-env.sh
SPARK_MASTER_HOST=hadoop001
SPARK_WORKER_CORES=2
SPARK_WORKER_MEMORY=2g
SPARK_WORKER_INSTANCES=1  # 这里以后可以任意设置
```

Spark Standalone模式的架构和Hadoop HDFS/YARN很类似的 1 master + n worker

启动spark，进入sbin目录，然后输入./start-all.sh，进入 http://192.168.0.100:8080/ 可以查看信息，然后在bin目录下，执行spark-shell --master spark://willhope-PC:7077  (后面这个spark://willhope-PC:7077在你的 http://192.168.0.100:8080/ 页面的顶部位置可见)，启动时间有些长。

###　三、使用Spark完成wordcount统计

在bin目录下，执行spark-shell --master spark://willhope-PC:7077 ，会出现一个spark图像；也可以使用spark-shell --master local[2],推荐使用后者，这样可以使机器负载低一些。

```
Welcome to
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ `/ __/  '_/
   /___/ .__/\_,_/_/ /_/\_\   version 2.4.4
      /_/

Using Scala version 2.11.12 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_211)
Type in expressions to have them evaluated.
Type :help for more information.

scala>

```

然后进行scala输入，可以将下面三行代码直接粘贴进去。注意，自己定义一个文件，用来操作wordcount统计

```s
# 仅仅只需要三行，就可以完成之前java写MR那些代码，但其实我们也可以使用Java8提供的函数式编程来简化代码

val file = spark.sparkContext.textFile("file:///home/willhope/data/hello.txt")
val wordCounts = file.flatMap(line => line.split("\t")).map((word => (word, 1))).reduceByKey(_ + _)
wordCounts.collect

运行结果：

scala> val file = spark.sparkContext.textFile("file:///home/willhope/data/hello.txt")
file: org.apache.spark.rdd.RDD[String] = file:///home/willhope/data/hello.txt MapPartitionsRDD[1] at textFile at <console>:23

scala> val wordCounts = file.flatMap(line => line.split("\t")).map((word => (word, 1))).reduceByKey(_ + _)
wordCounts: org.apache.spark.rdd.RDD[(String, Int)] = ShuffledRDD[4] at reduceByKey at <console>:25

scala> wordCounts.collect
res0: Array[(String, Int)] = Array((word,3), (hello,5), (world,3))  

```

# Spark SQL

### 一、Spark SQL的产生

SQL是关系型数据库的标准，现在大数据框架，对于那些关系型数据库的开发人员来说成本太高，企业也依赖关系型数据库，sql非常易于学习。

Hive:类似sql的HQL，但是底层应用的MR，这样性能很低。因此hive做了改进，hive on tez，hive on spark。hive on spark ==》shark，shark推出时很受欢迎，基于内存速度快，基于内存的列式存储（很大提升数据处理的效率），缺点：为了实现兼容spark，hql的解析、逻辑执行计划生成和优化依赖hive，仅仅只是把物理执行计划从mr作业替换成spark。后续shark终止，重新设计了Spark SQL，属于Spark社区。但是Hive on Spark依然存在，属于hive社区，进行维护。

Spark sql 支持多种数据源，多种优化技术，扩展性很好

### 二、SQL on Hadoop

1. hive，Facebook开源，当前80%的公司都在使用它进行离线处理，因此hive非常重要。sql==》MR，metastore：元数据，数据库，表，试图

2. impala，cloudera开发，sql不基于MR，但是对内存要求非常高，效率比hive高

3. presto，Facebook开源，京东使用

4. drill，近两年非常火的，可以访问hdfs、rdbms、json、hbase、mangodb、s3、hive

5. Spark SQL，可以使用dataframe/dataset api，metastore：元数据，可以访问hdfs、rdbms、json、hbase、mangodb、s3、hive，不仅仅有访问或者操作sql的功能，还有其他丰富的操作：外部数据源、优化

### 三、Spark SQL愿景

写更少的代码

读更少的数据

将优化交给底层优化器

### 四、Spark操作hive的多种方法

本节要掌握的

1. Spark1.x中的SQLContext/HiveContext的使用
2. Spark2.x中的SparkSession的使用
3. spark-shell/spark-sql的使用
4. thriftserver/beeline的使用
5. jdbc方式编程访问

- SQLContext的用法。

此项目放在[这里](https://github.com/Zhang-Yixuan/SparkProject/tree/master/SparkSQL/learning-project/Spark-SQL/project-1)，在IDEA中使用maven创建一个scala项目，然后设置pom.xml文件,可从提供的项目中直接复制，准备一份json文件

```json

people.json文件

{"name":"Michael"}
{"name":"Andy", "age":30}
{"name":"Justin", "age":19}
{"name":"zhangsan","age":20}

```

编写scala代码，与Java操作MR相同，都是引进一个包即可

```java
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

/**
 * SQLContext的使用
 */

object SQLContextApp {
  def main(args: Array[String]): Unit = {
    
    //设置要读取文件的路径，这里将这个路径 file:///home/willhope/sparkdata/people.json 直接写在了项目配置中，也可以将arg(0)更改为这个路径
    val path = args(0)

    //1.创建相应的context，配置相关的类，这里有些像MR中driver类加载map和reduce那样，并且设置好在本地运行setMaster("local[2]")
    val sparkConf = new SparkConf()
    sparkConf.setAppName("SQLContextApp").setMaster("local[2]")

    //2.加载上述配置
    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)


    //3.进行相关的处理，处理一个json文件
    val people = sqlContext.read.format("json").load(path)
    
    //这里会解析出json里面的字段
    people.printSchema()

    //显示json里的内容
    people.show()

    //4.养成好的习惯，要记得关闭资源
    sc.stop()
  }
}
```

在IDEA中执行后，我们会发现

```bash
//解析出的字段
root
 |-- age: long (nullable = true)
 |-- name: string (nullable = true)

//显示结果
+----+--------+
| age|    name|
+----+--------+
|null| Michael|
|  30|    Andy|
|  19|  Justin|
|  20|zhangsan|
+----+--------+

```

在生产中，肯定是在服务器上提交的，因此要将项目进行打包，到项目所在的目录中进行maven编译，执行mvn clean package -DskipTests，之后在项目所在目录下的target目录下就会有这个项目的jar包。然后写脚本执行在终端中提交。

Spark提交，下面这些是提交时要注意的参数

```bash
 /spark-submit \
  --class <main-class>
  --master <master-url> \
  --deploy-mode <deploy-mode> \
  --conf <key>=<value> \
  ... # other options
  <application-jar> \
  [application-arguments]

# 在工作当中，要把下面的这些代码放在shell文件中执行
  spark-submit \
  --name SQLContextApp \
  --class com.scala.spark.SQLContextApp \
  --master local[2] \
  /home/willhope/lib/sql-1.0.jar \
  /home/willhope/app/spark-2.1.0-bin-2.6.0-cdh5.15.1/examples/src/main/resources/people.json
```

- HiveContext，使用spark操作hive，编写的代码基本与SQLContext一致。

```java
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.hive.HiveContext

/**
 * HiveContextApp的基本操作
 * 在编译的时候，需要用--jars把连接mysql的jar包路径导入进来
 */
object HiveContextApp {
  def main(args: Array[String]): Unit = {

    //1.创建相应的context
    val sparkConf = new SparkConf()
    sparkConf.setAppName("HiveContextApp").setMaster("local[2]")

    val sc = new SparkContext(sparkConf)
    val hiveContext = new HiveContext(sc)


    //2.进行相关的处理
    hiveContext.table("emp").show()

    //3.关闭资源
    sc.stop()
  }
}

```

到项目所在的目录中进行maven编译，执行mvn clean package -DskipTests,之后在项目所在目录下的target目录下就会有这个项目的jar包。然后写脚本执行在终端中提交。这里与上面的不同，因为访问的是hive，因此无须添加要处理文件路径，但是要添加上访问mysql数据库的jar包。

```bash
  spark-submit \
  --name HiveContextApp \
  --class com.scala.spark.HiveContextApp \
  --jars ~/software/mysql-connector-java-5.1.27-bin.jar  \
  --master local[2] \
  /home/willhope/lib/sql-1.0.jar \
```

- Spark session是spark2.x推荐的，可以替代SQLContext和HiveContext的作用，而且代码量大大降低

```java

import org.apache.spark.sql.SparkSession

/**
 * SparkSession的使用
 */
object SparkSessionApp {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("SparkSessionApp").master("local[2]").getOrCreate()
    val people = spark.read.json("file:///home/willhope/sparkdata/people.json")
    people.show()

    spark.stop()
  }
}

```

- spark-shell的使用

在使用前，将hadoop所有项启动，hive启动起来。

要在Spark-shell访问hive，要先切换到hive目录下的conf目录，将hive-site.xml文件拷贝到Spark目录下的conf。cp hive-site.xml /home/willhope/app/spark-2.4.4-bin-2.6.0-cdh5.15.1/conf/，然后将hive目录下的lib下的mysql驱动jar包拷贝到spark目录下的jars中。也可以在spark目录下的bin目录下，添加路径执行./spark-shell --master local[2] --jars /home/willhope/software

配置好了一切后，启动./spark-shell --master local[2]，然后输入spark.sql("show tables").show，可访问hive下的表，注意，sql()的括号中用来写sql语句。

```bash

  1. spark.sql("show tables").show
  # 结果
 
  +--------+--------------------+-----------+
  |database|           tableName|isTemporary|
  +--------+--------------------+-----------+
  | default|          track_info|      false|
  | default|track_info_provin...|      false|
  +--------+--------------------+-----------+

  2. spark.sql("select * from track_info").show
  # 结果，这个表中有30万行数据，但只显示前20行，spark速度非常快。
  +---------------+--------------------+--------------------+-------------------+-------+--------+------+----+----------+
  |             ip|                 url|                  id|               time|country|province|  city|page|       day|
  +---------------+--------------------+--------------------+-------------------+-------+--------+------+----+----------+
  |   106.3.114.42|http://www.yihaod...|3T4QEMG2BQ93ATS98...|2013-07-21 11:24:56|   中国|  北京市|     -|   -|2013-07-21|
  |  58.219.82.109|http://www.yihaod...|3T489Y1W6TECTAKDF...|2013-07-21 13:57:11|   中国|  江苏省|无锡市|   -|2013-07-21|
  |  58.219.82.109|http://search.yih...|3T489Y1W6TECTAKDF...|2013-07-21 13:50:48|   中国|  江苏省|无锡市|   -|2013-07-21|
  |  58.219.82.109|http://search.yih...|3T489Y1W6TECTAKDF...|2013-07-21 13:57:16|   中国|  江苏省|无锡市|   -|2013-07-21|
  |  58.219.82.109|http://www.yihaod...|3T489Y1W6TECTAKDF...|2013-07-21 13:50:13|   中国|  江苏省|无锡市|   -|2013-07-21|
  |  218.11.179.22|http://www.yihaod...|3T3WSNPKS9M3AYS9Z...|2013-07-21 08:00:13|   中国|  河北省|邢台市|   -|2013-07-21|
  |  218.11.179.22|http://www.yihaod...|3T3WSNPKS9M3AYS9Z...|2013-07-21 08:00:20|   中国|  河北省|邢台市|   -|2013-07-21|
  | 123.123.202.45|http://search.1ma...|3T25C9M46884Y5G8V...|2013-07-21 11:55:28|   中国|  北京市|     -|   -|2013-07-21|
  | 123.123.202.45|http://t.1mall.co...|3T25C9M46884Y5G8V...|2013-07-21 11:55:21|   中国|  北京市|     -|   -|2013-07-21|
  |118.212.191.216|http://m.1mall.co...|3T22N5JG3SJD5425F...|2013-07-21 19:07:19|   中国|  江西省|南昌市|   -|2013-07-21|
  |   27.46.113.79|http://www.yihaod...|3T1KYPAPHWPSCWB3P...|2013-07-21 21:21:16|   中国|  广东省|深圳市|   -|2013-07-21|
  |   27.46.113.79|http://www.yihaod...|3T1KYPAPHWPSCWB3P...|2013-07-21 21:20:09|   中国|  广东省|深圳市|   -|2013-07-21|
  |   27.46.113.79|http://www.yihaod...|3T1KYPAPHWPSCWB3P...|2013-07-21 21:21:02|   中国|  广东省|深圳市|   -|2013-07-21|
  |   27.46.113.94|http://www.yihaod...|3T1KYPAPHWPSCWB3P...|2013-07-21 21:21:16|   中国|  广东省|深圳市|   -|2013-07-21|
  |   27.46.113.79|http://www.yihaod...|3T1KYPAPHWPSCWB3P...|2013-07-21 21:21:01|   中国|  广东省|深圳市|   -|2013-07-21|
  |   27.46.113.94|http://www.yihaod...|3T1KYPAPHWPSCWB3P...|2013-07-21 21:19:08|   中国|  广东省|深圳市|   -|2013-07-21|
  |   27.46.113.79|http://www.yihaod...|3T1KYPAPHWPSCWB3P...|2013-07-21 21:21:02|   中国|  广东省|深圳市|   -|2013-07-21|
  |   27.46.113.79|http://www.yihaod...|3T1KYPAPHWPSCWB3P...|2013-07-21 21:18:56|   中国|  广东省|深圳市|   -|2013-07-21|
  |   27.46.113.79|http://www.yihaod...|3T1KYPAPHWPSCWB3P...|2013-07-21 21:18:55|   中国|  广东省|深圳市|   -|2013-07-21|
  |   27.46.113.94|http://www.yihaod...|3T1KYPAPHWPSCWB3P...|2013-07-21 21:18:49|   中国|  广东省|深圳市|   -|2013-07-21|
  +---------------+--------------------+--------------------+-------------------+-------+--------+------+----+----------+
  only showing top 20 rows

```

- Spark sql的使用

其实与spark-shell用法相同，在spark目录下的bin目录下，添加路径执行./spark-sql --master local[2]，可以在浏览器输入 http://willhope-pc:4040/jobs/ 查看，这里启动后，其实就相当于启动了一个mysql控制台那种，可以直接写sql语句。在浏览器中刷新，可以看到刚才作业的完成情况。

  ```s
  # 创建一张表
  create table t(key string,value string);

  # 然后查看是否创建成功
  show tables;

  # 查看执行计划
  explain extended select a.key*(2+3), b.value from  t a join t b on a.key = b.key and a.key > 3;

  == Parsed Logical Plan ==
  'Project [unresolvedalias(('a.key * (2 + 3)), None), 'b.value]
  +- 'Join Inner, (('a.key = 'b.key) && ('a.key > 3))
    :- 'UnresolvedRelation `t`, a
    +- 'UnresolvedRelation `t`, b

  == Analyzed Logical Plan ==
  (CAST(key AS DOUBLE) * CAST((2 + 3) AS DOUBLE)): double, value: string
  Project [(cast(key#321 as double) * cast((2 + 3) as double)) AS (CAST(key AS DOUBLE) * CAST((2 + 3) AS DOUBLE))#325, value#324]
  +- Join Inner, ((key#321 = key#323) && (cast(key#321 as double) > cast(3 as double)))
    :- SubqueryAlias a
    :  +- MetastoreRelation default, t
    +- SubqueryAlias b
        +- MetastoreRelation default, t

  == Optimized Logical Plan ==
  Project [(cast(key#321 as double) * 5.0) AS (CAST(key AS DOUBLE) * CAST((2 + 3) AS DOUBLE))#325, value#324]
  +- Join Inner, (key#321 = key#323)
    :- Project [key#321]
    :  +- Filter (isnotnull(key#321) && (cast(key#321 as double) > 3.0))
    :     +- MetastoreRelation default, t
    +- Filter (isnotnull(key#323) && (cast(key#323 as double) > 3.0))
        +- MetastoreRelation default, t

  == Physical Plan ==
  *Project [(cast(key#321 as double) * 5.0) AS (CAST(key AS DOUBLE) * CAST((2 + 3) AS DOUBLE))#325, value#324]
  +- *SortMergeJoin [key#321], [key#323], Inner
    :- *Sort [key#321 ASC NULLS FIRST], false, 0
    :  +- Exchange hashpartitioning(key#321, 200)
    :     +- *Filter (isnotnull(key#321) && (cast(key#321 as double) > 3.0))
    :        +- HiveTableScan [key#321], MetastoreRelation default, t
    +- *Sort [key#323 ASC NULLS FIRST], false, 0
        +- Exchange hashpartitioning(key#323, 200)
          +- *Filter (isnotnull(key#323) && (cast(key#323 as double) > 3.0))
              +- HiveTableScan [key#323, value#324], MetastoreRelation default, t

  ```

- thriftserver/beeline的使用，前者是服务器，后者是客户端的意思，hive中的server称为hiveserver

```s

在spark目录中的sbin目录下启动./sbin/start-thriftserver.sh
可在 http://willhope-pc:4041/sqlserver/ 中查看，这里本应该是4040，但是如果之前启动了spark-sql，那么这个端口应该更改为4041，因为端口被占用，然后会自动加1

在spark目录中bin目录下启动beeline
./beeline -u jdbc:hive2://localhost:10000 -n willhope （注意这里的用户名是系统用户名）

启动成功后，出现 0: jdbc:hive2://localhost:10000> 

执行show tables;即可查看hive中表的情况
 
在 http://willhope-pc:4041/sqlserver/ 中刷新，可以查看作业执行情况。

修改thriftserver启动占用的默认端口号：
./start-thriftserver.sh  \
--master local[2] \
--jars ~/software/mysql-connector-java-5.1.27-bin.jar  \   这句如果添加了mysql的jar到jars目录中，则不用写
--hiveconf hive.server2.thrift.port=14000 

beeline -u jdbc:hive2://localhost:14000 -n willhope


thriftserver和普通的spark-shell/spark-sql有什么区别？
1）spark-shell、spark-sql都是一个spark  application；
2）thriftserver， 不管你启动多少个客户端(beeline/code)，只要连到一个server，永远都是一个spark application
这里已经解决了一个数据共享的问题，多个客户端可以共享数据；

```

- jdbc编程访问

```java
import java.sql.DriverManager

/**
 * 通过jdbc访问
 */

object SparkSQLThriftServerApp {
  def main(args: Array[String]): Unit = {

    Class.forName("org.apache.hive.jdbc.HiveDriver")

    val conn = DriverManager.getConnection("jdbc:hive2://localhost:10000","willhope","") //密码不写
    val pstmt = conn.prepareStatement("select province , cnt  from track_info_province_stat")
    val rs = pstmt.executeQuery()
    while (rs.next()){
      println("province:"+rs.getString("province")+"\t cnt:"+rs.getLong("cnt"))
    }

    rs.close()
    pstmt.close()
    conn.close()
  }
}


//结果
province:-	 cnt:923
province:上海市	 cnt:72898
province:云南省	 cnt:1480
province:内蒙古自治区	 cnt:1298
province:北京市	 cnt:42501
province:台湾省	 cnt:254
province:吉林省	 cnt:1435
province:四川省	 cnt:4442
province:天津市	 cnt:11042
province:宁夏	 cnt:352
province:安徽省	 cnt:5429
province:山东省	 cnt:10145
province:山西省	 cnt:2301
province:广东省	 cnt:51508
province:广西	 cnt:1681
province:新疆	 cnt:840
province:江苏省	 cnt:25042
province:江西省	 cnt:2238
province:河北省	 cnt:7294
province:河南省	 cnt:5279
province:浙江省	 cnt:20627
province:海南省	 cnt:814
province:湖北省	 cnt:7187
province:湖南省	 cnt:2858
province:澳门特别行政区	 cnt:6
province:甘肃省	 cnt:1039
province:福建省	 cnt:8918
province:西藏	 cnt:110
province:贵州省	 cnt:1084
province:辽宁省	 cnt:2341
province:重庆市	 cnt:1798
province:陕西省	 cnt:2487
province:青海省	 cnt:336
province:香港特别行政区	 cnt:45
province:黑龙江省	 cnt:1968

```

#### 五、DataFrame & Dataset

- RDD、DataFrame和Dataset是什么鬼？

1. 先看一下Spark中的RDD，DataFrame和Datasets的定义：

    Spark RDD

    RDD代表弹性分布式数据集。**它是数据库记录的只读分区集合**。 RDD是Spark的基本数据结构。它允许程序员以容错方式在大型集群上执行内存计算。

    Spark Dataframe

    与RDD不同，数据组以列的形式组织起来，**类似于关系数据库中的表**。它是一个不可变的分布式数据集合。 Spark中的DataFrame允许开发人员将数据结构(类型)加到分布式数据集合上，从而实现更高级别的抽象。

    Spark Dataset

    Apache Spark中的Dataset是DataFrame API的扩展，它提供了类型安全(type-safe)，面向对象(object-oriented)的编程接口。 Dataset利用优化器可以让用户通过类似于sql的表达式对数据进行查询。

2. RDD、DataFrame和DataSet的比较

  - Spark版本

    RDD – 自Spark 1.0起
    DataFrames – 自Spark 1.3起
    DataSet – 自Spark 1.6起

  - 数据表示形式

    RDD是分布在集群中许多机器上的数据元素的分布式集合。 RDD是一组表示数据的Java或Scala对象。

    DataFrame是命名列构成的分布式数据集合。 它在概念上类似于关系数据库中的表。

    Dataset是DataFrame API的扩展，提供RDD API的类型安全，面向对象的编程接口以及Catalyst查询优化器的性能优势和DataFrame API的堆外存储机制的功能。

  - 数据格式

    RDD可以轻松有效地处理结构化和非结构化的数据。 和Dataframe和DataSet一样，RDD不会推断出所获取的数据的结构类型，需要用户来指定它。

    DataFrame仅适用于结构化和半结构化数据。 它的数据以命名列的形式组织起来。

    DataSet可以有效地处理结构化和非结构化数据。 它表示行(row)的JVM对象或行对象集合形式的数据。 它通过编码器以表格形式(tabular forms)表示。
  
  - 编译时类型安全

    RDD提供了一种熟悉的面向对象编程风格，具有编译时类型安全性。

    DataFrame如果您尝试访问表中不存在的列，则持编译错误。 它仅在运行时检测属性错误。

    DataSet可以在编译时检查类型, 它提供编译时类型安全性。

  - 序列化

    RDD每当Spark需要在集群内分发数据或将数据写入磁盘时，它就会使用Java序列化。序列化单个Java和Scala对象的开销很昂贵，并且需要在节点之间发送数据和结构。

    Spark DataFrame可以将数据序列化为二进制格式的堆外存储（在内存中），然后直接在此堆内存上执行许多转换。无需使用java序列化来编码数据。它提供了一个Tungsten物理执行后端，来管理内存并动态生成字节码以进行表达式评估。

    DataSet在序列化数据时，Spark中的数据集API具有编码器的概念，该编码器处理JVM对象与表格表示之间的转换。它使用spark内部Tungsten二进制格式存储表格表示。数据集允许对序列化数据执行操作并改善内存使用。它允许按需访问单个属性，而不会消灭整个对象。

  - 垃圾回收
  
    RDD 创建和销毁单个对象会导致垃圾回收。

    DataFrame 避免在为数据集中的每一行构造单个对象时引起的垃圾回收。

    DataSet因为序列化是通过Tungsten进行的，它使用了off heap数据序列化，不需要垃圾回收器来摧毁对象

  - 效率/内存使用

    RDD 在java和scala对象上单独执行序列化时，效率会降低，这需要花费大量时间。

    DataFrame 使用off heap内存进行序列化可以减少开销。 它动态生成字节代码，以便可以对该序列化数据执行许多操作。 无需对小型操作进行反序列化。

    DataSet它允许对序列化数据执行操作并改善内存使用。 因此，它可以允许按需访问单个属性，而无需反序列化整个对象。

  - 编程语言支持

    RDD 提供Java，Scala，Python和R语言的API。 因此，此功能为开发人员提供了灵活性。

    DataFrame同样也提供Java，Scala，Python和R语言的API。

    DataSet 的一些API目前仅支持Scala和Java，对Python和R语言的API在陆续开发中。
  
  - 聚合操作(Aggregation)

    RDD API执行简单的分组和聚合操作的速度较慢。

    DataFrame API非常易于使用。 探索性分析更快，在大型数据集上创建汇总统计数据。

    Dataset中，对大量数据集执行聚合操作的速度更快。

结论

1. 当我们需要对数据集进行底层的转换和操作时， 可以选择使用RDD
2. 当我们需要高级抽象时，可以使用DataFrame和Dataset API。
3. 对于非结构化数据，例如媒体流或文本流，同样可以使用DataFrame和Dataset API。
4. 我们可以使用DataFrame和Dataset 中的高级的方法。 例如，filter, maps, aggregation, sum, SQL queries以及通过列访问数据等，如果您不关心在按名称或列处理或访问数据属性时强加架构（例如列式格式）。另外，如果我们想要在编译时更高程度的类型安全性。RDD提供更底层功能， DataFrame和Dataset则允许创建一些自定义的结构，拥有高级的特定操作，节省空间并高速执行。为了确保我们的代码能够尽可能的利用Tungsten优化带来的好处，推荐使用Scala的 Dataset API（而不是RDD API）。Dataset即拥有DataFrame带来的relational transformation的便捷，也拥有RDD中的functional transformation的优势。