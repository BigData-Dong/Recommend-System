package com.BookCrossing;

import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

/*
 * @ClassName: BXDataModelBuilder
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/12 22:24
 * @Description:  DataModelBuilder
 */
public class BXDataModelBuilder implements DataModelBuilder {

    @Override
    public DataModel buildDataModel(FastByIDMap<PreferenceArray> trainingData) {
        return new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(trainingData));
    }

}
