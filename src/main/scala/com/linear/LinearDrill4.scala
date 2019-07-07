package com.linear

import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.ml.tuning.{ParamGridBuilder, TrainValidationSplit}
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/*
 * @ClassName: LinearDrill4
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/4 15:37
 * @Description: TrainValidation Split
      准备训练和测试数据
      使用ParamGridBuilder 构造一个参数网格
      使用TrainValidationSplit来选择模型和参数
      CrossValidator需要一个Estimator，一个评估器参数集合，和一个Evaluator
      运行训练校验分离，选择最好的参数
      在测试数据上做预测，模型是参数组合中执行最好的一个

 */
object LinearDrill4 {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName(this.getClass.getSimpleName).setMaster("local")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")
    val sqlContext = new SQLContext(sc)

    val data = sqlContext.read.format("libsvm").load("G:\\机器学习-数据\\推荐系统\\数据\\sample_linear_regression_data.txt")
    // 将数据进行划分
    val Array(training,test) = data.randomSplit(Array(0.8,0.2),seed = 1)

    val lr = new LinearRegression()
      .setMaxIter(10)

    // 利用参数网格设置参数
    val paramGrid = new ParamGridBuilder()
        // 设置惩罚系数alpha
        .addGrid(lr.elasticNetParam,Array(0.0,0.5,1.0))
        // 正则化（regularization） 保留所有特征，但是减少参数的值。
        .addGrid(lr.regParam,Array(0.1,0.01))
        .addGrid(lr.fitIntercept)
        .build()

    // TrainValidationSplit 将尝试所有的组合选择最佳的模型
    /*
     * 除了CrossValidator Spark，还提供了用于超参数调整的TrainValidationSplit。
     * TrainValidationSplit仅对参数的每个组合进行一次评估，而在CrossValidator的情况下，则不是k次。
     * 因此，它较便宜，但在训练数据集不够大时不会产生可靠的结果。
     * 与CrossValidator不同，TrainValidationSplit创建一个（训练，测试）数据集对。
     * 它使用trainRatio参数将数据集分成这两个部分。 例如，trainRatio = 0.75，TrainValidationSplit将生成训练和测试数据集对，其中75％的数据用于训练，25％用于验证。
     */
    val trainValidationSplit = new TrainValidationSplit()
        .setEstimator(lr)
        .setEstimatorParamMaps(paramGrid)
        .setEvaluator(new RegressionEvaluator())
        .setTrainRatio(0.8)

    val model = trainValidationSplit.fit(training)
    model.transform(test).select("label","features","prediction").show()

    training.show()
    sc.stop()
  }
}
