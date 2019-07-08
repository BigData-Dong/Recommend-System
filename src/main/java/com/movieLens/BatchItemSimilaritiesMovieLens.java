package com.movieLens;

import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.precompute.FileSimilarItemsWriter;
import org.apache.mahout.cf.taste.impl.similarity.precompute.MultithreadedBatchItemSimilarities;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.precompute.BatchItemSimilarities;
import org.apache.mahout.cf.taste.similarity.precompute.SimilarItemsWriter;
import org.omg.SendingContext.RunTime;

import java.io.File;

/*
 * @ClassName: BatchItemSimilaritiesMovieLens
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/8 16:44
 * @Description:  批量生成物品推荐结果
 */
public class BatchItemSimilaritiesMovieLens {

    private BatchItemSimilaritiesMovieLens(){};

    public static void main(String[] args) throws Exception{

        if(args.length != 1){
            System.err.printf("Needs MovieLens 1M dataset as arugument!");
            System.exit(0);
        }

        File resultFile = new File(System.getProperty("java.io.tmpdir"),"similarities.csv");
        DataModel dataModel = new MovieLensDataModel(new File(args[0]));
        // 对数似然相似度
        ItemSimilarity itemSimilarity = new LogLikelihoodSimilarity(dataModel);
        // base item recommender
        ItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel,itemSimilarity);
        // 批项目相似度
        /*
            物品相似度
            推荐的数量
         */
        BatchItemSimilarities batchItemSimilarities = new MultithreadedBatchItemSimilarities(recommender,5);

        SimilarItemsWriter writer = new FileSimilarItemsWriter(resultFile);
        /*
            1. 线程数
            2. 最大运行的秩序时间（hourse）
            3. 输出的文件
         */
        int numSimilar = batchItemSimilarities.computeItemSimilarities(Runtime.getRuntime().availableProcessors(),1,writer);
        System.out.println("Computed " + numSimilar + " for " + dataModel.getNumItems() + " item and saved them to " + resultFile.getAbsolutePath());
    }
}
