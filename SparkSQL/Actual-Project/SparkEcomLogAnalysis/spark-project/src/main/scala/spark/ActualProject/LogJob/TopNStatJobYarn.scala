package spark.ActualProject.LogJob

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.mutable.ListBuffer


/**
 * TopN 统计 Spark 作业
 */
object TopNStatJobYarn {


  def main(args: Array[String]): Unit = {


    if(args.length != 2){
      println("Usage : TopNStatJobYarn <inputPath> <day>")
      System.exit(1)
    }

    val Array(inputPath , day) = args

    //我们自定义的结构体中time是string类型，但是spark会将此类型改变为int类型，因此使用config方法让其禁止更改
    val spark = SparkSession.builder().appName("TopNStatJob")
      .config("spark.sql.sources.partitionColumnTypeInference.enabled","false")
      .master("local[2]").getOrCreate()

    val accessDF = spark.read.format("parquet").load(inputPath)

//    accessDF.printSchema()
//    accessDF.show(false)

    StatDAO.deleteData(day)


    //使用dataFrame统计,最受学生欢迎的TopN课程
//    videoAccessTopNStat(spark,accessDF,day)

    //使用sql进行统计
    videoAccessTopNStat2(spark,accessDF,day)

    //按照地市进行统计TopN课程
    cityAccessTopNStat(spark , accessDF,day)

    //按照流量进行统计
    videoTrafficsTopNStat(spark , accessDF,day)

    spark.stop()

  }

  /**
   * 使用dataFrame统计，通常推荐使用df
   * 最受学生欢迎的TopN课程
   * @param spark
   * @param accessDF
   * @return
   */
  def videoAccessTopNStat(spark: SparkSession, accessDF: DataFrame,day:String) = {


    import spark.implicits._

    //agg是聚合函数
    val videoAccessTopNDF = accessDF.filter($"day" === day && $"cmsType" === "video")
      .groupBy("day","cmsId")
      .agg(count("cmsId")
        .as("times")).orderBy($"times".desc)

    videoAccessTopNDF.show(false)



  }


  /**
   * 使用SQL的方式进行统计
   */
  def videoAccessTopNStat2(spark: SparkSession, accessDF: DataFrame,day:String) = {

    accessDF.createOrReplaceTempView("access_logs")
    val videoAccessTopNDF = spark.sql("select day,cmsId, count(1) as times from access_logs " +
          "where day='20170511' and cmsType='video' " +
          "group by day,cmsId order by times desc")

     videoAccessTopNDF.show(false)

    /**
     * 将统计结果写入到MySQL中
     */
    try {
      videoAccessTopNDF.foreachPartition(partitionOfRecords => {
        val list = new ListBuffer[DayVideoAccessStat]

        partitionOfRecords.foreach(info => {
          val day = info.getAs[String]("day")
          val cmsId = info.getAs[Long]("cmsId")
          val times = info.getAs[Long]("times")

          /**
           * 不建议大家在此处进行数据库的数据插入
           */

          list.append(DayVideoAccessStat(day, cmsId, times))
        })

        StatDAO.insertDayVideoAccessTopN(list)
      })
    } catch {
      case e:Exception => e.printStackTrace()
    }

  }

  /**
   * 按照地市进行统计欢迎课程TopN
   * @param spark
   * @param accessDF
   * @return
   */
  def cityAccessTopNStat(spark: SparkSession, accessDF: DataFrame,day:String) = {
    import spark.implicits._

    //agg是聚合函数
    val cityAccessTopNDF = accessDF.filter($"day" === day && $"cmsType" === "video")
      .groupBy("day","city","cmsId")
      .agg(count("cmsId")
        .as("times")).orderBy($"times".desc)

    cityAccessTopNDF.show(false)


    val top3DF = cityAccessTopNDF.select(cityAccessTopNDF("day"),
      cityAccessTopNDF("cmsId") ,
      cityAccessTopNDF("city"),
        cityAccessTopNDF("times"),
      row_number().over(Window.partitionBy(cityAccessTopNDF("city")).orderBy(cityAccessTopNDF("times").desc)).as("times_rank")
    ).filter("times_rank <=3") //.show(false)

    /**
     * 将统计结果写入到MySQL中
     */
    try {
      top3DF.foreachPartition(partitionOfRecords => {
        val list = new ListBuffer[DayCityVideoAccessStat]

        partitionOfRecords.foreach(info => {
          val day = info.getAs[String]("day")
          val cmsId = info.getAs[Long]("cmsId")
          val city = info.getAs[String]("city")
          val times = info.getAs[Long]("times")
          val timesRank = info.getAs[Int]("times_rank")

          /**
           * 不建议大家在此处进行数据库的数据插入
           */

          list.append(DayCityVideoAccessStat(day, cmsId , city , times , timesRank))
        })

        StatDAO.insertDayCityVideoAccessTopN(list)
      })
    } catch {
      case e:Exception => e.printStackTrace()
    }

  }

  /**
   * 按照流量进行统计
   */
  def videoTrafficsTopNStat(spark: SparkSession, accessDF:DataFrame,day:String): Unit = {
    import spark.implicits._

    val cityAccessTopNDF = accessDF.filter($"day" === day && $"cmsType" === "video")
      .groupBy("day","cmsId").agg(sum("traffic").as("traffics"))
      .orderBy($"traffics".desc)
    //.show(false)

    /**
     * 将统计结果写入到MySQL中
     */
    try {
      cityAccessTopNDF.foreachPartition(partitionOfRecords => {
        val list = new ListBuffer[DayVideoTrafficsStat]

        partitionOfRecords.foreach(info => {
          val day = info.getAs[String]("day")
          val cmsId = info.getAs[Long]("cmsId")
          val traffics = info.getAs[Long]("traffics")
          list.append(DayVideoTrafficsStat(day, cmsId,traffics))
        })

        StatDAO.insertDayVideoTrafficsAccessTopN(list)
      })
    } catch {
      case e:Exception => e.printStackTrace()
    }

  }


}
