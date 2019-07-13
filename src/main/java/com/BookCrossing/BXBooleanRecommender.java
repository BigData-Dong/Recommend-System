package com.BookCrossing;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CachingUserSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.Collection;
import java.util.List;

/*
 * @ClassName: BXBooleanRecommender
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/12 21:23
 * @Description: 推荐系统-> 0 推荐  1 不推荐
 *
 *      优化：  CachingUserSimilarity 优化
 *              ThresholdUserNeighborhood
 *
 */
public class BXBooleanRecommender implements Recommender {

    private Recommender recommender;

    BXBooleanRecommender(DataModel dataModel) throws TasteException {
        // 最大自然相似  CachingUserSimilarity 优化
        UserSimilarity similarity = new CachingUserSimilarity(new LogLikelihoodSimilarity(dataModel),dataModel);
        //取最大的负无穷  没有限制
//        UserNeighborhood neighborhood = new NearestNUserNeighborhood(100,Double.NEGATIVE_INFINITY,similarity,dataModel,1.0);
        //当相似度达到 0.5 的时候认为使我们的领局
        // 抽样  1.0
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.5,similarity,dataModel,1.0);
        recommender = new GenericUserBasedRecommender(dataModel,neighborhood,similarity);
    }

    @Override
    public List<RecommendedItem> recommend(long userID, int howMany) throws TasteException {
        return recommender.recommend(userID,howMany,null);
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
