package com.imooc.scala.spark

import org.apache.spark.sql.SparkSession

/**
 * Dataset的操作
 */
object DatasetApp {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("DatasetApp").master("local[2]").getOrCreate()

    //注意需要导入隐式转换
    import spark.implicits._

    val path = "file:///home/willhope/sparkdata/sales.csv"

    //spark解析csv文件，显然dataframe拿到，然后在转为dataset
    val df = spark.read.option("header","true").option("inferSchema","true").csv(path)
    df.show()

    //df转换为ds
    val ds = df.as[Sales]
    ds.map(line=>line.itemId).show()



    spark.stop()
  }

  case class Sales(transactionId:Int,customerId:Int,itemId:Int,amountPaid:Double)

}
