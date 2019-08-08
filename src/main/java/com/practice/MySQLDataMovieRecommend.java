package com.practice;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.jdbc.ConnectionPoolDataSource;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.logging.Logger;


/*
 * @ClassName: MySQLDataMovieRecommend
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/16 13:33
 * @Description: mahout推荐案列 --->  Jdbc model implement
 *
 *  注: 连接Mysql会超时
 *      需要在my.ini文件中配置如下参数
 *          bind-address="127.0.0.1"
            wait_timeout=31536000
            interactive_timeout=31536000
            connect_timeout=31536000
 *
 */
@SuppressWarnings("unused")
public class MySQLDataMovieRecommend {

    private MySQLDataMovieRecommend(){}

    public static void main(String[] args) throws TasteException,IOException {

        File resultFile = new File("G:\\机器学习-数据\\推荐系统\\MovieLens\\","MysqlMoiveRecommend.txt");

        // MySQL Connection
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setDatabaseName("mahout");
        mysqlDataSource.setServerName("127.0.0.1");
        mysqlDataSource.setUser("root");
        mysqlDataSource.setPassword("123");
        ConnectionPoolDataSource connectionPoolDataSource = new ConnectionPoolDataSource(mysqlDataSource);

        DataModel dataModel = new MySQLJDBCDataModel(connectionPoolDataSource,"taste_preferences2","user_id","item_id","preference",null);


        // Recommendations
        UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
//        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.3,similarity,dataModel);
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(3,similarity,dataModel);
        UserBasedRecommender recommender = new GenericUserBasedRecommender(dataModel,neighborhood,similarity);

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile))){
            // every user recommend 3 movieLine
            LongPrimitiveIterator userIDs = dataModel.getUserIDs();
            while (userIDs.hasNext()){
                long userid = userIDs.nextLong();
                List<RecommendedItem> recommend = recommender.recommend(userid, 3);
                String line = userid + " : ";
                for (RecommendedItem item : recommend) {
                    line += item.getItemID() + "-" + item.getValue() + ",";
                }
                if(line.endsWith(",")){
                    line = line.substring(0,line.length()-1);
                }
                writer.write(line);
                writer.flush();
                writer.newLine();
            }
        } catch (IOException e) {
            resultFile.delete();
            throw e;
        }
        System.out.println("Recommend for " + dataModel.getNumUsers() + "users and saved them to " + resultFile.getAbsolutePath());
    }
}
