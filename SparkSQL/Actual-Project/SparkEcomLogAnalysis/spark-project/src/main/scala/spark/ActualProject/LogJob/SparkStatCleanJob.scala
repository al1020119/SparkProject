package spark.ActualProject.LogJob

import org.apache.spark.sql.{SaveMode, SparkSession}

/**
 * 使用Spark完成我们的数据清洗操作
 */
object SparkStatCleanJob {

  def main(args: Array[String]) {
    val spark = SparkSession.builder().appName("SparkStatCleanJob")
      .config("spark.sql.parquet.compression.codec","gzip")
      .master("local[2]").getOrCreate()

    val accessRDD = spark.sparkContext.textFile("/home/willhope/SparkProject/data/access.log")

    //accessRDD.take(10).foreach(println)

    //RDD ==> DF
    val accessDF = spark.createDataFrame(accessRDD.map(x => AccessConvertUtil.parseLog(x)),
      AccessConvertUtil.struct)

//    accessDF.printSchema()
//    accessDF.show(false)

    //下面这几句注释是调优点
    //coalesce这个方法用来制定输出的文件个数
    //partitionBy用来进行按天分组
    //mode方法用来解决已经存在文件夹的问题
    accessDF.coalesce(1).write.format("parquet").mode(SaveMode.Overwrite)
      .partitionBy("day").save("/home/willhope/sparkdata/clean")

    spark.stop
  }
}
