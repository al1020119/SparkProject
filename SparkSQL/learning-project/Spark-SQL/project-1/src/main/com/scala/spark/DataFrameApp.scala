package com.scala.spark

import org.apache.spark.sql.SparkSession

/**
 * DataFrame API基本操作
 */
object DataFrameApp {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("DataFrameApp").master("local[2]").getOrCreate()

    //将json文件加载成一个dataframe
    val peopleDF = spark.read.format("json").load("file:///home/willhope/sparkdata/people.json")

    //输出dataframe对应的schema信息
    peopleDF.printSchema()

    //输出数据集的前20条，可以自己在括号中写数量
    peopleDF.show()

    //输出某个列的内容
    peopleDF.select("name").show()

    //输出年龄列，并将年龄+10,select name from table
    peopleDF.select(peopleDF.col("name") , peopleDF.col("age")+10).show()

    //起个别名,输出年龄列，并将年龄+10,select name , age + 10  as age2 from table;
    peopleDF.select(peopleDF.col("name") , (peopleDF.col("age")+10).as("age2")).show()

    //根据某一列的值进行过滤： select * from table where age > 19
    peopleDF.filter(peopleDF.col("age")>19).show()

    //根据某一列进行分组，然后在进行局和操作：select age , count(1) from table group by age
    peopleDF.groupBy("age").count().show()

    spark.stop()
  }

}
