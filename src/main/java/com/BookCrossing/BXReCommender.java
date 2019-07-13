package com.BookCrossing;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import java.util.Collection;
import java.util.List;

/*
 * @ClassName: BXReCommender
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/12 15:31
 * @Description: 基于评分的推荐 -> 自定义Recommender
 */
@SuppressWarnings("unused")
public class BXReCommender implements Recommender {

    private Recommender recommender;

    BXReCommender(DataModel dataModel) throws TasteException {
        UserSimilarity userSimilarity = new EuclideanDistanceSimilarity(dataModel);
        /*
         * 1. 最多的相似用户
         * 2. 最小的相似度
         * 3. 用户相似度
         * 4. 数据模型
         */
        UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(100,0.2,userSimilarity,dataModel);
        recommender = new GenericUserBasedRecommender(dataModel,userNeighborhood,userSimilarity);
    }


    @Override
    public List<RecommendedItem> recommend(long userID, int howMany) throws TasteException {
        return recommender.recommend(userID, howMany, null);
    }

    @Override
    public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer) throws TasteException {
        return recommender.recommend(userID,howMany,rescorer);
    }

    @Override
    public float estimatePreference(long userID, long itemID) throws TasteException {
        return recommender.estimatePreference(userID,itemID);
    }

    @Override
    public void setPreference(long userID, long itemID, float value) throws TasteException {
        recommender.setPreference(userID,itemID,value);
    }

    @Override
    public void removePreference(long userID, long itemID) throws TasteException {
        recommender.removePreference(userID,itemID);
    }

    @Override
    public DataModel getDataModel() {
        return recommender.getDataModel();
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        recommender.refresh(alreadyRefreshed);
    }
}
