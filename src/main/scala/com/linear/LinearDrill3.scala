package com.linear

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/*
 * @ClassName: LinearDrill3
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/4 15:00
 * @Description: 模型优化
 *    ParamGridBuilder 参数网格
 *    CrossValider 交叉认证
 */
object LinearDrill3 {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName(this.getClass.getSimpleName).setMaster("local")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")
    val sqlContext = new SQLContext(sc)

    val training = sqlContext.createDataFrame(Seq(
      (0L,"a b c d e spark",1.0),
      (1L,"b d",0.0),
      (2L,"spark f g h",1.0),
      (3L,"g d a y",0.0),
      (4L,"b spark who",1.0),
      (5L,"g d a y",0.0),
      (6L,"spark fly",1.0),
      (7L,"was mapreduce",0.0),
      (8L,"e spark program",1.0),
      (9L,"a e c l",0.0),
      (10L,"spark compile",1.0),
      (11L,"hadoop software",0.0)
    )).toDF("id","text","label")

    val tokenizer = new Tokenizer()
        .setInputCol("text")
        .setOutputCol("words")

    val hashTF = new HashingTF()
        .setInputCol(tokenizer.getOutputCol)
        .setOutputCol("features")

    val lr = new LogisticRegression()
        .setMaxIter(10)

    val pipeline = new Pipeline()
      .setStages(Array(tokenizer,hashTF,lr))

    // 构建参数网格
    val paramGrid = new ParamGridBuilder()
        .addGrid(hashTF.numFeatures,Array(10,100,1000))
        .addGrid(lr.regParam,Array(0.1,0.01))
        .build()

    // 构建转换器  setNumFolds(2) 生产 15 或者更大
    /**
      * 交叉验证
      * CrossValidator首先将数据集分成一组折叠，这些折叠用作单独的训练和测试数据集。
      * 例如，k = 3倍，CrossValidator将生成3个（训练，测试）数据集对，每个数据集使用2/3的数据进行训练，
      * 1/3进行测试。 为了评估一个特定的ParamMap，CrossValidator通过在3个不同的（训练，测试）
      * 数据集对上拟合Estimator来计算3个模型的平均评估度量。
      * 在确定最佳ParamMap之后，CrossValidator最终使用最好的ParamMap和整个数据集重新拟合Estimator。
      *
      *
      * 示例：通过交叉验证进行模型选择
      * 以下示例演示如何使用CrossValidator从参数网格中进行选择。
      * 请注意，通过参数网格的交叉验证是昂贵的。 例如，在下面的示例中，
      * 参数网格具有3个值，用于hashingTF.numFeatures，2个值用于lr.regParam，CrossValidator使用2个折叠。
      * 这被乘以（3×2）×2 = 12个不同的模型被训练。
      * 在现实的设置中，尝试更多参数并使用更多的折叠（k = 3，k = 10是常见的）是常见的。
      * 换句话说，使用CrossValidator可能非常昂贵。 然而，它也是一种成熟的方法，
      * 用于选择比启发式手动调谐更具统计学意义的参数。
      */
    val cv = new CrossValidator()
        .setEstimator(pipeline)
        .setEstimatorParamMaps(paramGrid)
        .setEvaluator(new BinaryClassificationEvaluator())
       // 验证几次
        .setNumFolds(2)

    val model = cv.fit(training)
    val test = sqlContext.createDataFrame(Seq(
      (4L,"spark i j k"),
      (5L,"l m n"),
      (6L,"spark h g"),
      (7L,"apache hadoop")
    )).toDF("id","text")

     model.transform(test).select("id","text","probability"
       ,"prediction").show()

    sc.stop()
  }
}
