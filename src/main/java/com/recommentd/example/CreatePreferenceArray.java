package com.recommentd.example;

/*
 * @ClassName: CreatePreferenceArray
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/5 14:53
 * @Description: 推荐系统 - 创建偏好数组 - 存储单个用户偏好
 */
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import tachyon.security.User;

public class CreatePreferenceArray {

    private CreatePreferenceArray(){}

    public static void main(String[] args) {
        PreferenceArray user1Pref = new GenericUserPreferenceArray(2);
        // 数组ID   User  ID
        user1Pref.setUserID(0,1L);
        // 数组ID   Item  ID
        user1Pref.setItemID(0,101L);
        // 数组ID   hoppy  ID
        user1Pref.setValue(0,3.0f);

        user1Pref.setItemID(1,102L);
        user1Pref.setValue(1,4.0f);

        System.out.println(user1Pref);
    }




}
