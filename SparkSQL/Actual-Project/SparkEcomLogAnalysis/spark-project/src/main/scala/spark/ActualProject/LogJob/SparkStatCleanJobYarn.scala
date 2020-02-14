package spark.ActualProject.LogJob

import org.apache.spark.sql.{SaveMode, SparkSession}

/**
 * 使用Spark完成我们的数据清洗操作，运行在Yarn上
 */
object SparkStatCleanJobYarn {

  def main(args: Array[String]) {

    if(args.length != 2){
      println("Usage : SparkStatCleanJobYarn <inputPath> <outPath>")
      System.exit(1)
    }

    val Array(inputPath , outPath) = args

    val spark = SparkSession.builder()
      .config("spark.sql.parquet.compression.codec","gzip")
      .getOrCreate()

    val accessRDD = spark.sparkContext.textFile(inputPath)


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
      .partitionBy("day").save(outPath)

    spark.stop
  }
}
