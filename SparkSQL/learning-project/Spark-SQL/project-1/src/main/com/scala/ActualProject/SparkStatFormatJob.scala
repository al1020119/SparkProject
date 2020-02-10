package spark.ActualProject

import org.apache.spark.sql.SparkSession

/**
 * 清洗数据
 * 第一步清洗：先抽取出我们所需要的指定列的数据
 */

object SparkStatFormatJob {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("SparkStatFormatJob").master("local[2]").getOrCreate()

    val access = spark.sparkContext.textFile("file:///home/willhope/sparkdata/10000_access.log")

    //先查看一下数据
//    access.take(10).foreach(println)

//    access.map(line => {
//      val splits = line.split(" ")
//      val ip = splits(0)
//      val url = splits(11).replaceAll("\"","")
//      val traffic = splits(9)
//
//      /**
//       * 原始日志中第三个和第四个字段拼接起来就是完整的访问时间
//       */
//      val time = splits(3)+ " "+splits(4)
//
//      //使用元组进行展示
//      (ip,DataUtils.parse(time),url,traffic)
//
//    }).take(10).foreach(println)

    //将数据写入到文件系统
    access.map(line => {
      val splits = line.split(" ")
      val ip = splits(0)
      val url = splits(11).replaceAll("\"","")
      val traffic = splits(9)

      /**
       * 原始日志中第三个和第四个字段拼接起来就是完整的访问时间
       */
      val time = splits(3)+ " "+splits(4)

      //使用元组进行展示
      DataUtils.parse(time) + "\t" + url + "\t" + traffic + "\t" + ip
    }).saveAsTextFile("file:///home/willhope/sparkdata/output")

    spark.stop()
  }

}
