package com.recommentd.example;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

import java.io.File;
import java.io.IOException;

/*
 * @ClassName: IREvaluatorIntro
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/7 14:20
 * @Description: 推荐系统 -> 模型评估IR
 * 5.c评价算法，计算推荐系统的准确率，召回率等指标
 */
public class IREvaluatorIntro {

    private IREvaluatorIntro(){}

    public static void main(String[] args) throws IOException, TasteException {
        // 测试环境使用
        RandomUtils.useTestSeed();
        // 读取数据
        DataModel model = new FileDataModel(new File("G:\\机器学习-数据\\推荐系统\\MovieLens\\ua.base"));

        RecommenderIRStatsEvaluator irStatsEvaluator = new GenericRecommenderIRStatsEvaluator();

        RecommenderBuilder builder = dataModel -> {
            // 用户相似度  皮尔森系数相似度
            UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(100,similarity,dataModel);
            return new GenericUserBasedRecommender(dataModel,neighborhood,similarity);
        };
        /*
         * @param recommenderBuilder
         * 它通过public Recommender buildRecommender(DataModel model)定义推荐系统创建的方式；
         * @param dataModelBuilder
         * 数据模型创建的方式，如果已经创建好了数据模型，一般这个参数可以为null
         * @param dataModel
         * 推荐系统使用的数据模型
         * @param rescorer
         * 推荐排序的方式，一般情况下可以为null
         * @param at
         * 这个参数起的名字挺奇怪的，它用来定义计算准确率的时候，
         * 一个user可以拥有的相关项items的最大个数，相关项item定义为user对这个item的评价是超过了relevanceThreshold的
         * @param relevanceThreshold
         * 和at一起使用定义一个user的相关项
         * @return {@link IRStatistics} with resulting precision, recall, etc.
         * @throws TasteException
         *           if an error occurs while accessing the {@link DataModel}
         */
        IRStatistics statistics = irStatsEvaluator.evaluate(builder,null,model /*推荐5个商品评估准确度*/,null,5,GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,1.0);
        // 求准率  准确率，
        System.out.println(statistics.getPrecision());
        // 求全率  召回率
        System.out.println(statistics.getRecall());
        //  2 ^ 1
        System.out.println(statistics.getF1Measure());

    }


}
