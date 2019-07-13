package com.BookCrossing;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.model.DataModel;

import java.io.File;
import java.io.IOException;

/*
 * @ClassName: BXRecommenderEvaluator
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/12 16:08
 * @Description: 推荐系统 -> 平均绝对值评估器
 *  args  G:\机器学习-数据\推荐系统\Book-Crossing\BX-Book-Ratings.csv
 */
public class BXRecommenderEvaluator {

    private BXRecommenderEvaluator(){}

    public static void main(String[] args) throws IOException, TasteException {
//        DataModel dataModel = new BXDataModel(new File(args[0]),false);
//        RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
//        double score = evaluator.evaluate(new BXRecommenderBuilder(),null,dataModel,0.9,0.3);
//        System.out.println("HAE score is " + score);

        // 无评分推荐
        DataModel dataModel = new BXDataModel(new File(args[0]),true);
        RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();

        double score = evaluator.evaluate(new BXBooleanRecommendBuilder(),null,dataModel,0.9,0.3);
        System.out.println("MAE score is " + score);
    }
}
