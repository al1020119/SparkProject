package spark.ActualProject.LogJob

import java.sql.{Connection, DriverManager, PreparedStatement}

/**
 * MySQL操作工具类
 */
object MySQLUtils {

  /**
   * 获取数据库的连接
   */
  def getConnection() = {
    DriverManager.getConnection("jdbc:mysql://localhost:3306/spark_stat_project?user=root&password=123456&ssl=false")
  }

  /**
   * 释放数据库资源
   * @param connection
   * @param pstmt
   */
  def release(connection: Connection, pstmt: PreparedStatement) = {
    try {
      if (pstmt != null) {
        pstmt.close()
      }
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      if (pstmt != null) {
        pstmt.close()
      }
    }
  }


  def main(args: Array[String]): Unit = {
    println(getConnection())
  }

}
