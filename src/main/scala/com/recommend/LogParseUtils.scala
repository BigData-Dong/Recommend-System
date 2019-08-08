package com.recommend

import org.apache.spark.mllib.recommendation._

/*
 * @ClassName: MoviceLinesSpark
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/8/4 11:19
 * @Description: Spark Recommend MovieLines
 */

object LogParseUtils {
  // Define parse function
  def parseMovie(str: String): Movie = {
    val fields = str.split("::")
    assert(fields.size == 3)
    Movie(fields(0).toInt,fields(1).toString,Seq(fields(2)))
  }
  def parseUser(str: String): User = {
    val fields = str.split("::")
    assert(fields.size == 5)
    User(fields(0).toInt,fields(1).toString,fields(2).toInt,fields(3).toInt,fields(4).toString)
  }
  def parseRating(str: String):Rating = {
    val fields = str.split("::")
    assert(fields.size == 4)
    Rating(fields(0).toInt,fields(1).toInt,fields(2).toDouble)
  }

}


//G:\机器学习-数据\推荐系统\MovieLens\ml-1m\movies.dat
case class Movie(movieID:Int,title:String,genres:Seq[String])
//G:\机器学习-数据\推荐系统\MovieLens\ml-1m\\users.dat
case class User(userId:Int,gender:String,age:Int,occupation:Int,zip:String)
