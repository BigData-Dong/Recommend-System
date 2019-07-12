package com.movieLens;

import org.apache.commons.io.Charsets;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.common.iterator.FileLineIterable;

import javax.xml.stream.events.Characters;
import java.io.*;
import java.util.regex.Pattern;

/*
 * @ClassName: MovieLensDataModel
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/7/7 20:40
 * @Description: 电影 推荐数据模型：
 *                  将数据文件中的 :: 转换为 , 并写入行的文件路径
 */
public class MovieLensDataModel extends FileDataModel {

    private static String COLON_DELIMINITER = "::";
    private static Pattern COLON_DELIMITER_PATTERN = Pattern.compile(COLON_DELIMINITER);

    public MovieLensDataModel(File ratingFile) throws IOException {
        super(convertFile(ratingFile));
    };

    private static File convertFile(File orginalFile) throws IOException{
        File resultFile = new File("G:\\机器学习-数据\\推荐系统\\MovieLens","ratings.dat");
//        File resultFile = new File(System.getProperty("java.io.tmpdir"),"ratings.dat");
        if(resultFile.exists()){
            resultFile.delete();
        }
        try(Writer writer = new OutputStreamWriter(new FileOutputStream(resultFile), Charsets.UTF_8)){
            for(String line : new FileLineIterable(orginalFile,false)){
                int lastIndexOf = line.lastIndexOf(COLON_DELIMINITER);
                if(lastIndexOf < 0){
                    throw new IOException("Invalid data! message [ " + line + " ] ");
                }
                String subLine = line.substring(0,lastIndexOf);
                String convertedSubLine = COLON_DELIMITER_PATTERN.matcher(subLine).replaceAll(",");
                writer.write(convertedSubLine);
                writer.write("\n");
                writer.flush();
            }
        }catch (IOException e){
            resultFile.delete();
            throw  e;
        }
        return resultFile;
    }

}
