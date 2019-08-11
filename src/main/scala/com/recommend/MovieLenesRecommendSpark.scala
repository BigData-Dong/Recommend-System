package com.recommend

import org.apache.spark.mllib.evaluation.RegressionMetrics
import org.apache.spark.mllib.recommendation.{ALS, Rating}
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
      """
        | select title,rmax,rmin,ucnt
        | from
        |( select product,max(rating) as rmax,
        | min(rating) as rmin,
        | count(distinct(user)) as ucnt
        | from ratings group by product) ratingsCNT
        | join movies on product = movieID
        | order by ucnt desc
      """.stripMargin).show()

    val mostActiveUser = sparkSql.sql(
      """
        | select user,count(*) as cnt
        | from ratings group by user order by cnt desc limit 10
      """.stripMargin).show()

    val result = sparkSql.sql(
      """
        | select  distinct title,rating
        | from ratings join movies on product = movieID
        | where user=4169 and rating > 4
      """.stripMargin).show()

    //ALS
    val splits = ratingRDD.randomSplit(Array(0.8,0.2),0L)
    val trainingSet = splits(0).cache()
    val testSet = splits(1).cache()
//    trainingSet.count()
//    testSet.count()

    //隐含因子20  需要迭代10
    val model = new ALS().setRank(20).setIterations(10).run(trainingSet)

    //为最活跃的用户进行推荐
    val recomForTopUse = model.recommendProducts(4169,5)

    val movieTitle = movieDF.map(array => (array(0),array(1))).collectAsMap()
    recomForTopUse.map(rating => {
      // 获取标题   电影评分
      (movieTitle(rating.product),rating.rating)
    }).foreach(println)

    // 预测
    val testUserProduct = testSet.map{
      case Rating(user,product,rating) => (user,product)
    }
//    testUserProduct.take(2)

    val testUserProductPredict = model.predict(testUserProduct)
//    testUserProductPredict.take(10).mkString("\n")

    val testSetPair = testSet.map{
      case Rating(user,product,rating) => ((user,product),rating)
    }

    val predictionsPair = testUserProductPredict.map{
      case Rating(user,product,rating) => ((user,product),rating)
    }

    val joinTestPredict = testSetPair.join(predictionsPair)
    //测试误差
    val mae = joinTestPredict.map{
      case ((user,product),(ratingT,ratingP)) =>
        val err = ratingT - ratingP
        Math.abs(err)
    }.mean()

    //FP  实际ratingTrue <= 1 预测ratingP >=4  实际为假预测为真
    val fp = joinTestPredict.filter{
      case ((user,product),(ratingT,ratingP)) =>
        ratingT <= 1 & ratingP >= 4
    }

    // 调用模型进行预测
    val ratingTP = joinTestPredict.map{
      case ((user,product),(ratingT,ratingP)) =>
        (ratingP,ratingT)
    }
    val evalutor = new RegressionMetrics(ratingTP)
    //误差
    evalutor.meanAbsoluteError
    //均方根误差
    evalutor.rootMeanSquaredError

    sc.stop()
  }
}
