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

### 四、从hive平滑过度到Spark SQL

本节要掌握的

```
Spark1.x中的SQLContext/HiveContext的使用
Spark2.x中的SparkSession的使用
spark-shell/spark-sql的使用
thriftserver/beeline的使用
jdbc方式编程访问
```

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

在生产中，肯定是在服务器上提交的，因此要将项目进行打包，然后写脚本执行。到项目所在的目录中进行maven编译，执行mvn clean package -DskipTests，之后在项目所在目录下的target目录下就会有这个项目的jar包。然后在终端中提交。

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
  --class com.imooc.spark.SQLContextApp \
  --master local[2] \
  /home/willhope/lib/sql-1.0.jar \
  /home/willhope/app/spark-2.1.0-bin-2.6.0-cdh5.15.1/examples/src/main/resources/people.json
```

- HiveContext