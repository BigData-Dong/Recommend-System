package com.movieLens;

import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/*
 * @ClassName: UserRecommenderMovieLens
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/8 20:12
 * @Description: 用户推荐
 */
public class UserRecommenderMovieLens {

    private UserRecommenderMovieLens(){}

    public static void main(String[] args) throws Exception {

        if(args.length != 1){
            System.err.print("");
        }

        File resultFile = new File(System.getProperty("java.io.tmpdir"),"userRcomd.csv");
        DataModel dataModel = new MovieLensDataModel(new File(args[0]));
        UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(dataModel);
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(100,userSimilarity,dataModel);
        Recommender recommender = new GenericUserBasedRecommender(dataModel,neighborhood,userSimilarity);
       // 缓存推荐
        Recommender cachingRecommender = new CachingRecommender(recommender);
        // Evaluate 模型评估
        RMSRecommenderEvaluator rms = new RMSRecommenderEvaluator();
        RecommenderBuilder recommenderBuilder = (model) -> {
          UserSimilarity userSimilarity1 = new PearsonCorrelationSimilarity(model);
          UserNeighborhood neighborhood1 = new NearestNUserNeighborhood(100,userSimilarity,model);
          return new GenericUserBasedRecommender(model,neighborhood,userSimilarity);
        };

        double score = rms.evaluate(recommenderBuilder,null,dataModel,0.9,0.5);
        System.out.print("RMSE score is " + score);

        try(PrintWriter writer = new PrintWriter(resultFile)){
            for(int userID=1;userID <= dataModel.getNumUsers();userID ++ ){
                List<RecommendedItem> recommendedItems = cachingRecommender.recommend(userID,2);
                // every user id
                String line = userID + " : ";
                for (RecommendedItem recommendedItem : recommendedItems){
                    line += recommendedItem.getItemID() + ":" + recommendedItem.getValue()+",";
                }
                if (line.endsWith(",")){
                    line.substring(0,line.length());
                }
                writer.write(line);
                writer.write("\n");
            }
        }catch (IOException e){
           resultFile.delete();
           throw e;
        }
        System.out.println("REcommended for " + dataModel.getNumUsers() + " users and saved them to " + resultFile.getAbsolutePath());
    }
}
