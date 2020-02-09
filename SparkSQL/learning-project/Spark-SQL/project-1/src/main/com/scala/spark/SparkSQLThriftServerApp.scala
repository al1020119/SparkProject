package com.scala.spark

import java.sql.DriverManager

/**
 * 通过jdbc访问
 */

object SparkSQLThriftServerApp {
  def main(args: Array[String]): Unit = {

    Class.forName("org.apache.hive.jdbc.HiveDriver")

    val conn = DriverManager.getConnection("jdbc:hive2://localhost:10000","willhope","")
    val pstmt = conn.prepareStatement("select province , cnt  from track_info_province_stat")
    val rs = pstmt.executeQuery()
    while (rs.next()){
      println("province:"+rs.getString("province")+"\t cnt:"+rs.getLong("cnt"))
    }

    rs.close()
    pstmt.close()
    conn.close()
  }
}
