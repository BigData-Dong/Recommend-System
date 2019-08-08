package com.recommend

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/*
 * @ClassName: MovieLinesRecommendSpark
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/8/4 17:35
 * @Description: MovieLens Recommend System Spark
 */
object MovieLenesRecommendSpark {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName(this.getClass.getSimpleName).setMaster("local[4]")
    val sc = new SparkContext(conf)
    val sparkSql = new SQLContext(sc)
    sc.setLogLevel("WARN")
    val ratingText = sc.textFile("G:\\机器学习-数据\\推荐系统\\MovieLens\\ml-1m\\ratings.dat")

    // Ratin analyst
    val ratingRDD = ratingText.map(LogParseUtils.parseRating).cache()
    println("Total number of ratings: " + ratingRDD.count())
    println("Total nunber of movies rated: " + ratingRDD.map(_.product).distinct().count())
    println("Total nunber of users who rated movies : " + ratingRDD.map(_.user).distinct().count())

    // Create DataFrames
    import sparkSql.implicits._
    val ratingDF = ratingRDD.toDF()
//    println(ratingDF.printSchema())

    val movieDF = sc.textFile("G:\\机器学习-数据\\推荐系统\\MovieLens\\ml-1m\\movies.dat")
        .map(LogParseUtils.parseMovie).toDF()

    val userDF = sc.textFile("G:\\机器学习-数据\\推荐系统\\MovieLens\\ml-1m\\users.dat")
        .map(LogParseUtils.parseUser).toDF()

    // register table
    ratingDF.registerTempTable("ratings")
    movieDF.registerTempTable("movies")
    userDF.registerTempTable("users")

    sparkSql.sql(
 
    sc.stop()
  }
}
