package com.scala.spark

import org.apache.spark.sql.SparkSession

/**
 * SparkSession的使用
 */
object SparkSessionApp {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("SparkSessionApp").master("local[2]").getOrCreate()
    val spark2 = SparkSession.builder()
    val people = spark.read.json("file:///home/willhope/sparkdata/people.json")
    people.show()

    spark.stop()
  }
}
