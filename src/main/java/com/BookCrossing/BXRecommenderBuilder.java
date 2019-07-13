package com.BookCrossing;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

/*
 * @ClassName: BXRecommenderBuilder
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/12 16:05
 * @Description: 图书推荐系统 ->  自定义RecommenderBuilder
 */
@SuppressWarnings("unused")
public class BXRecommenderBuilder implements RecommenderBuilder {

    @Override
    public Recommender buildRecommender(DataModel dataModel) throws TasteException {
        return new BXReCommender(dataModel);
    }
}
