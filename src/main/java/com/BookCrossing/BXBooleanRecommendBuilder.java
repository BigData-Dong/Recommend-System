package com.BookCrossing;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

/*
 * @ClassName: BXBooleanRecommendBuilder
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/12 22:10
 * @Description:
 */
public class BXBooleanRecommendBuilder implements RecommenderBuilder {
    @Override
    public Recommender buildRecommender(DataModel dataModel) throws TasteException {
        return new BXBooleanRecommender(dataModel);
    }
}
