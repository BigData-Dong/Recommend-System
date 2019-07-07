package com.recommentd.example;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.*;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.LongPair;
import org.apache.mahout.common.RandomUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/*
 * @ClassName: EvaluatorIntro
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/5 21:01
 * @Description: 推荐实例4-->模型评估 : 对推介结果进行评估
 *
 *  AverageAbsoluteDifferenceRecommenderEvaluator  RMSRecommenderEvaluator  用来评估结果
 *
 */
public class EvaluatorIntro {

    private EvaluatorIntro(){}

    public static void main(String[] args) throws IOException, TasteException {

        // 仅在测试使用  保证每次结果都一样
        RandomUtils.useTestSeed();
        // 读取数据
        DataModel model = new FileDataModel(new File("G:\\机器学习-数据\\推荐系统\\MovieLens\\ua.base"));
        /*
         * 是求取推荐值与实际值之间的绝对值abs
         * average变量则将这个绝对值加起来，
         * 最终结果则是average中所有数据的平均值。
            使用一个average保存结果，
            对测试集的每一个user的每个item都进行一次测试，
            如果推荐结果不为空，则进入processOneEstimate处理，
            将推荐值与实际值的差距加入average，
            最后所有结果的平均值即为最终的评价结果
         */
        RecommenderEvaluator averagEvaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
        // RMSRecommenderEvaluator，processOneEstimate则是求取推荐值与实际值的平方值。
        RecommenderEvaluator rmsEvaluator = new RMSRecommenderEvaluator();

        // 构造推荐
        RecommenderBuilder recommenderBuilder = dataModel -> {
            // 欧几里得
            UserSimilarity similarity = new EuclideanDistanceSimilarity(dataModel);
//            余弦相似度
//            UserSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
            // 相似度  皮尔森系数
            //            UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
            // 找出最近几个用户
            UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(100,similarity,dataModel);
            return new GenericUserBasedRecommender(dataModel,userNeighborhood,similarity);
        };

        /*
            评估      trainingPercentage : 每个用户的 70%来做评估  30 % 的数据作为测试
                      evaluationPercentage 1.0 代表所有的用户来参与评估
          */
        double score = averagEvaluator.evaluate(recommenderBuilder,null,model,0.7,1.0);
        double rmmse = rmsEvaluator.evaluate(recommenderBuilder,null,model,0.7,1.0);
        // score  越接近 0 越好
        System.out.println("均方差 : " + score);
        System.out.println("平方根误差 ：" + rmmse);

    }
}

