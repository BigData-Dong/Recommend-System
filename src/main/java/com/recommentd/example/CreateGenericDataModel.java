package com.recommentd.example;

/*
 * @ClassName: CreateGenericDataModel
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/5 15:26
 * @Description: 推荐系统 - FastByIDMap保存多个用户偏好
 */
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.PreferenceArray;


public class CreateGenericDataModel {

    private CreateGenericDataModel(){}

    public static void main(String[] args) {
        FastByIDMap<PreferenceArray> fastByIDMap = new FastByIDMap<PreferenceArray>();

        PreferenceArray user1Preference = new GenericUserPreferenceArray(2);
        user1Preference.setUserID(0,1L);
        user1Preference.setItemID(0,101L);
        user1Preference.setValue(0,3.0f);
        user1Preference.setItemID(0,102L);
        user1Preference.setValue(0,4.0f);

        PreferenceArray user2Preference = new GenericUserPreferenceArray(2);
        user2Preference.setUserID(0,2L);
        user2Preference.setItemID(0,101L);
        user2Preference.setValue(0,3.0f);
        user2Preference.setItemID(1,102L);
        user2Preference.setValue(1,4.0f);
        fastByIDMap.put(1L,user1Preference);
        fastByIDMap.put(2L,user2Preference);
        GenericDataModel model = new GenericDataModel(fastByIDMap);
        System.out.println(fastByIDMap);
        System.out.println(model);
    }

}
