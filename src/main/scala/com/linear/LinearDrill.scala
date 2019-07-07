package com.linear

import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}
/*
 * @ClassName: LinearDrill
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/3 22:48
 * @Description: 
 */
object LinearDrill {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName(this.getClass.getSimpleName).setMaster("local[2]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    sc.setLogLevel("WARN")
    val training = sqlContext.createDataFrame(Seq(
      (1.0,Vectors.dense(0.0,1.1,0.1)),
      (0.0,Vectors.dense(2.0,1.1,-1.0)),
      (0.0,Vectors.dense(2.0,1.3,1.0)),
      (1.0,Vectors.dense(0.0,1.2,-0.1))
    )).toDF("label","features")
    val lr = new LogisticRegression()
    lr.setRegParam(0.1)
    lr.setMaxIter(10)

    val model = lr.fit(training)

    val paramMap = ParamMap(lr.maxIter -> 20)
      .put(lr.maxIter -> 30)
      .put(lr.regParam -> 0.1,lr.threshold -> 0.55)

    val paramMap2 = ParamMap(lr.probabilityCol -> "myProbability")
    val paramMapCombined = paramMap ++ paramMap2

    val model2 = lr.fit(training,paramMapCombined)


    println(model2.parent.extractParamMap())
    val test = sqlContext.createDataFrame(Seq(
      (1.0,Vectors.dense(-1.0,1.5,1.3)),
      (0.0,Vectors.dense(0.0,2.2,-1.0)),
      (1.0,Vectors.dense(3.0,2.0,-1.5))
    )).toDF("label","features")

    model.transform(test)
      .select("label", "features", "probability", "prediction")
      .rdd.foreach{
          println(_)
      }
    }
}
