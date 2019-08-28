package com.recom.offline;

import com.recom.commom.RedisUtil;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import redis.clients.jedis.Jedis;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSON;

/*
 * @ClassName: UserItemSimilarityTableRedisWriter
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/8/25 15:27
 * @Description: Created by Dylan
 */
public class UserItemSimilarityTableRedisWriter {

    private DataModel dataModel = null;
    private Jedis jedis = null;
    private CountDownLatch latch = new CountDownLatch(1);

    public UserItemSimilarityTableRedisWriter(DataModel dataModel){
        this.dataModel = dataModel;
        this.jedis = RedisUtil.getJedis();
    }

    public void storeToRedis(){
        Executors.newSingleThreadExecutor().submit(() -> {
            process();
            latch.countDown();
        });
    }

    private void process(){
        try{
            LongPrimitiveIterator iterator = dataModel.getUserIDs();
            while (iterator.hasNext()){
                long userID = iterator.nextLong();
                FastIDSet iDSet = dataModel.getItemIDsFromUser(userID);
                String key = "UI:" + userID;
                String value = JSON.toJSONString(iDSet.toArray());
                jedis.set(key,value);
                System.out.println("Stored User:" + key);
            }
        }catch (TasteException te){
            te.printStackTrace();
        }
    }

    public void waitUtilDone() throws InterruptedException{
        latch.await();
    }
}
