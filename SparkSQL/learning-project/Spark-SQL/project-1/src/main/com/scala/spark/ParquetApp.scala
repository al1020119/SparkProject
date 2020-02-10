package com.imooc.scala.spark

import org.apache.spark.sql.SparkSession

/**
 * parquet文件操作
 */

object ParquetApp {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("ParquetApp").master("local[2]").getOrCreate()

    //标准写法，可以使用spark.read.load(路径)，但是此方法只适用于parquet文件
    val userDF = spark.read.format("parquet").load("file:///home/willhope/sparkdata/users.parquet")
    //上面这句也可以更改为
    //spark.read.format("parquet").option("path","file:///home/willhope/sparkdata/users.parquet").load().show()


    userDF.printSchema()
    userDF.show()

    //将查询结果写回
    userDF.select("name","favorite_color").write.format("json").save("file:///home/willhope/app/tmp/jsonout")



    spark.stop()

  }
}
