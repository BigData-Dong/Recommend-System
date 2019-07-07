package com.recommentd.example;


import org.apache.mahout.cf.taste.impl.model.file.*;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.impl.similarity.*;
import org.apache.mahout.cf.taste.impl.neighborhood.*;
import org.apache.mahout.cf.taste.impl.recommender.*;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.similarity.*;
import org.apache.mahout.cf.taste.recommender.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * @ClassName: RecommenderIntro
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/5 15:58
 * @Description: 文件系统
 */
public class RecommenderIntro {

    private RecommenderIntro(){}

    public static void main(String[] args) throws Exception {
        // 读取文件的DataModel
        DataModel model = new FileDataModel(new File("G:\\机器学习-数据\\推荐系统\\MovieLens\\ua.base"));
        // 用 皮尔森系数  获得相似度
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        // 取前几个用户作为
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(20, similarity, model);
        // 推荐
        Recommender recommender = new GenericUserBasedRecommender(model,neighborhood,similarity);

        // 给那个用于推荐几个商品
        List<RecommendedItem> list = recommender.recommend(2,10);
        for (RecommendedItem item : list) {
            System.out.println(item);
        }
    }

}
