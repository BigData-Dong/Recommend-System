package com.BookCrossing;

import com.recommentd.example.IREvaluatorIntro;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import java.io.File;
import java.io.IOException;

/*
 * @ClassName: BXBooleanRecommenderEvaluator
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/12 22:12
 * @Description:
 *  args
 */
public class BXBooleanRecommenderEvaluator {

    private BXBooleanRecommenderEvaluator(){}

    public static void main(String[] args) throws IOException,TasteException {

        DataModel dataModel = new BXDataModel(new File(args[0]),false);
        RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();

        // at -> 基于多少的推荐进行评估
        // 相关度
        // 评分百分比
        IRStatistics statistics = evaluator.evaluate(new BXBooleanRecommendBuilder(), new BXDataModelBuilder(),dataModel,null,3,Double.NEGATIVE_INFINITY,1.0);

        //  准确率，召回率  2 ^ 1
        System.out.println("Preci is " + statistics.getPrecision() + " ; Recall is " + statistics.getRecall() + "; F1 is " + statistics.getF1Measure());


    }

}
