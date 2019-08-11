package com.dylan


import java.io.File

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.evaluation.RegressionMetrics
import org.apache.spark.mllib.recommendation.{MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD


/*
 * @ClassName: MovieLensALS
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/8/11 18:39
 * @Description: 推荐系统实战
 */
object MovieLensALS {

  //1. Define a rating elicitation function
  def elicitateRating(movies: Seq[(Int,String)]) = {
    val prompt = "please rate the following movie(1-5(best) or 0 if not seen!)"
    println(prompt)
    val ratings = movies.flatMap{x =>
      var rating:Option[Rating] = None
      var valid = false
      while(!valid){
         println(x._2 + " : ")
         try{
           val r = Console.readInt()
           if(r > 5 || r < 0){
               println(prompt)
           }else{
             valid = true
             if(r > 0){
               rating = Some(Rating(0,x._1,r))
             }
           }
         }catch {
           case e:Exception => println(prompt)
         }
      }
      rating match {
        case Some(r) => Iterable(r)
        case None => Iterator.empty
      }
    }

    if(ratings.isEmpty){
      error("No ratings provided!")
    }else{
      ratings
    }
  }
  //2. Define a RMSE computation function
  def computeRms(model: MatrixFactorizationModel, data:RDD[Rating]) = {
    val prediction = model.predict(data.map(x => (x.user,x.product)))
    val value = data.map(x => ((x.user,x.product),x.rating))
    val predDataJoined = prediction.map(x=> ((x.user,x.product),x.rating)).join(value).values
    // 均方根误差  RegressionMetricsD 回归度量
    new RegressionMetrics(predDataJoined).rootMeanSquaredError
  }

  //3. Main
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    if(args.length != 1){
      println("Usage: moveLensHomeDir")
      sys.exit(1)
    }

   //3.1 Setup env
    val conf = new SparkConf().setAppName(this.getClass.getSimpleName)
      .set("spark.executor,memory","500m")
    val sc = new SparkContext(conf)

  //3.2 Load ratings data and know your data
    val movieLensHomeDir = args(0)
    val ratings = sc.textFile(new File(movieLensHomeDir,"ratings.dat").toString).map{ line =>
      val fields = line.split("::")
      // timestamp，user, product, rating
      (fields(3).toLong % 10,Rating(fields(0).toInt,fields(1).toInt,fields(2).toDouble))
    }
    val movies = sc.textFile(new File(movieLensHomeDir,"movies.dat").toString).map{ line =>
      val fields = line.split("::")
      // movieId，movieName
      (fields(0).toLong,fields(1).toString)
    }
    val numRatings = ratings.count()
    val numUser = ratings.map(x => x._2.user).distinct().count()
    val numMovie = ratings.map(_._2.product).distinct().count()

    println("Got " + numRatings + " ratings from " + numUser + " users on " + numMovie + " movies.")
  //3.3 Elicitate personal rating

  //3.4 Split data into train(68%),validation(20%) and test(20%)

  //3.5 train model and optimize model with validation set

  //3.6 Create a baseline and evaluate model with test set

  //3.7 Make a personal recommendation
  }


}
