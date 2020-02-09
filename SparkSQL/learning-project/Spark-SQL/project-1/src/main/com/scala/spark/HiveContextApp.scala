package com.scala.spark

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
