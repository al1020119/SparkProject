package com.scala.spark

import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

/**
 * DataFrameRDD和RDD的相互操作
 */
object DataFrameRDDApp {



  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("DataFrameApp").master("local[2]").getOrCreate()

    //inferReflection(spark)
    program(spark)

    spark.stop()
  }


  def program(spark: SparkSession) = {
    //sparkContext可以获取到rdd，textfile可以从hdfs上读取文件，或者从本地获取文件
    val rdd = spark.sparkContext.textFile("file:///home/willhope/sparkdata/infos.txt")

    val infoRDD = rdd.map(_.split(",")).map(line => Row(line(0).toInt, line(1), line(2).toInt))

    val structType = StructType(Array(StructField("id",IntegerType,true),
      StructField("name",StringType,true),
      StructField("age",IntegerType,true)
    ))

    val infoDF = spark.createDataFrame(infoRDD,structType)
    infoDF.printSchema()
    infoDF.show()

    infoDF.filter(infoDF.col("age") > 30).show()

    //如果对编程不了解，也可以采用sql的方式来解决，创建一张临时的表，然后可以直接使用spark-sql
    infoDF.createOrReplaceTempView("infos")
    spark.sql("select * from infos where age>30").show()

  }


  def inferReflection(spark: SparkSession): Unit = {

    //sparkContext可以获取到rdd，textfile可以从hdfs上读取文件，或者从本地获取文件
    val rdd = spark.sparkContext.textFile("file:///home/willhope/sparkdata/infos.txt")

    //导入一个隐式转换
    import spark.implicits._
    //第一个map将每行记录按照逗号分割。然后第二个map将每行的信息，然后将结果转换成DataFrame，这里需要一个隐式转换
    val infoDF = rdd.map(_.split(",")).map(line => Info(line(0).toInt, line(1), line(2).toInt)).toDF()

    //显示表中的数据
    infoDF.show()

    //显示年龄大于30岁的
    infoDF.filter(infoDF.col("age") > 30).show()

    //如果对编程不了解，也可以采用sql的方式来解决，创建一张临时的表，然后可以直接使用spark-sql
    infoDF.createOrReplaceTempView("infos")
    spark.sql("select * from infos where age>30").show()
  }

  case class Info(id : Int, name : String, age : Int)

}
