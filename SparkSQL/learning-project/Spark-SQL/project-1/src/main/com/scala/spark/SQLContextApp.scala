package com.scala.spark

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

/**
 * SQLContext的使用
 */
object SQLContextApp {
  def main(args: Array[String]): Unit = {

    val path = args(0)

    //1.创建相应的context
    val sparkConf = new SparkConf()
    sparkConf.setAppName("SQLContextApp").setMaster("local[2]")

    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)


    //2.进行相关的处理，处理一个json文件
    val people = sqlContext.read.format("json").load(path)
    people.printSchema()
    people.show()

    //3.关闭资源
    sc.stop()
  }
}
