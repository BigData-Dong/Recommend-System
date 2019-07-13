package com.BookCrossing;


import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.similarity.file.FileItemSimilarity;
import org.apache.mahout.common.iterator.FileLineIterable;

import java.io.*;
import java.util.regex.Pattern;

/*
 * @ClassName: BXDataModel
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/12 15:05
 * @Description: 图书推荐系统
 *
 */
@SuppressWarnings("unused")
public class BXDataModel extends FileDataModel {

    private static String COLON_DELIMINITER = ";";
    // 非数值型分号结束的数值 将它剔除
    private static Pattern NON_DIGIT_SENICOLON_DELIMITER =  Pattern.compile("[^0-9;]");

    public BXDataModel(File dataFile,Boolean ignoresRatings) throws IOException {
        super(convertFile(dataFile,ignoresRatings));
    }

    private static File convertFile(File dataFile,boolean ignoresRatings) throws IOException {
        File resultFile = new File("G:\\机器学习-数据\\推荐系统\\Book-Crossing","ratings.dat");
        if(resultFile.exists()){
            resultFile.delete();
        }
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile))){
           for(String line : new FileLineIterable(dataFile,true)){
               // 为 0 的评分数据忽略掉
               if(line.endsWith("\"0\"")){
                   continue;
               }
               String convertedLine = NON_DIGIT_SENICOLON_DELIMITER.matcher(line).replaceAll("").replace(";",",");
               // 过滤掉非法数据
               if(convertedLine.contains(",,")){
                   continue;
               }
               // 是否忽略 得分列
               if(ignoresRatings){
                   convertedLine = convertedLine.substring(0,convertedLine.lastIndexOf(","));
               }
               writer.write(convertedLine);
               writer.newLine();
               writer.flush();
           }
        }catch (IOException e){
            resultFile.delete();
            throw  e;
        }
        return resultFile;
    }

}
