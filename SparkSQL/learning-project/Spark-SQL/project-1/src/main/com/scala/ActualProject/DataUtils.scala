package spark.ActualProject

import java.util.{Date, Locale}

import org.apache.commons.lang3.time.FastDateFormat

/**
 * 日期时间解析类
 */
object DataUtils {

    //输入文件日期时间格式,但是在后续解析过程中，发现simpleDateFormat这个方法有些问题，比如出现1970
//    val YYYYMMDDHHMM_TIME_FORMAT = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z",Locale.ENGLISH)
    val YYYYMMDDHHMM_TIME_FORMAT = FastDateFormat.getInstance("dd/MMM/yyyy:HH:mm:ss Z",Locale.ENGLISH)

    val TARGET_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")

    /**
     * 获取时间  yyyy-MM-dd HH:mm:ss
     * @param time
     */
    def parse(time : String)= {
      TARGET_FORMAT.format(new Date(getTime(time)))
    }

    def getTime(time : String) = {
      try {
        YYYYMMDDHHMM_TIME_FORMAT.parse(time.substring(time.indexOf("[") + 1, time.lastIndexOf("]"))).getTime
      }catch {
        case e : Exception =>{
          0l
        }
      }
    }

  //测试一下
  def main(args: Array[String]): Unit = {
    println(parse("[10/Nov/2016:00:01:02 +0800]"))
  }

}
