package spark.ActualProject.LogJob

/**
 * 每天每个城市的视频统计实体类
 * @param day
 * @param cmsId
 * @param city
 * @param times
 * @param timesRank
 */
case class DayCityVideoAccessStat(day:String,cmsId:Long,city:String,times:Long,timesRank:Int)