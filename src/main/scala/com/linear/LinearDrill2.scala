package com.linear

import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}
/*
 * @ClassName: LinearDrill2
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/4 13:49
 * @Description: Pipeline 使用
 */
object LinearDrill2 {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName(this.getClass.getSimpleName).setMaster("local[2]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    sc.setLogLevel("WARN")

    val training = sqlContext.createDataFrame(Seq(
      (1L,"b d",0.0),
      (2L,"spark f g h",1.0),
      (3L,"hadoop mapreduce",0.0)
    )).toDF("id","text","label")

    // 分词器
    val tokenizer = new Tokenizer()
    tokenizer.setInputCol("text")
    tokenizer.setOutputCol("words")
//    val tokenizerTranfrom = tokenizer.transform(training)

    val hashTF = new HashingTF()
    hashTF.setInputCol(tokenizer.getOutputCol)
    hashTF.setOutputCol("features")
//    hashTF.transform(tokenizerTranfrom).show()

    val lr = new LogisticRegression()
        .setMaxIter(10).setRegParam(0.1)
    //设置管道
    val pipeline = new Pipeline()
        .setStages(Array(tokenizer,hashTF,lr))
    //训练管道
//    val model = pipeline.fit(training)

    //保存管道到磁盘
//    pipeline.save("sparkML-LRpipeline")
//    model.save("sparkML-LRModel")


    //加载模型
    val model2 = PipelineModel.load("sparkML-LRModel")

    val test = sqlContext.createDataFrame(Seq(
      (4L,"spark i j k"),
      (5L,"l m n"),
      (6L,"spark h g"),
      (7L,"apache hadoop")
    )).toDF("id","text")

    model2.transform(test).select("id","text","probability"
    ,"prediction").show()

    sqlContext.clearCache()
    sc.stop()
  }
}
