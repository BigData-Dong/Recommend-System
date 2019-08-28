package com.recom.offline;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import org.apache.commons.io.Charsets;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.common.iterator.FileLineIterable;

import javax.xml.stream.events.Characters;
import java.io.*;
import java.net.URL;
import java.util.regex.Pattern;

/*
 * @ClassName: GroupLensDataModel
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/8/24 13:57
 * @Description:
 */

public final class GroupLensDataModel extends FileDataModel {

    private static final String COLON_DELIMTER = "::";
    private static final Pattern COLON_DELIMITER_PATTERN = Pattern.compile(COLON_DELIMTER);

    public GroupLensDataModel() throws IOException {
        this(readResourceToTempFile("/../resources/ratings.dat"));
    }

    /**
     * @param ratingsFile GroupLens ratings.dat file in its native format
     * @throws IOException if an error occurs while reading or writing files
     */
    public GroupLensDataModel(File ratingsFile) throws IOException {
        super(convertGLFile(ratingsFile));
    }

    private static File convertGLFile(File originalFile) throws IOException {
        // Now translate the file; remove ccommas,the convert "::" delimiter to comma
        File resultFile = new File(new File(System.getProperty("java.io.tmpdir")),"ratings.txt");
        if(resultFile.exists()){
            resultFile.delete();
        }
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(resultFile),Charsets.UTF_8);
            for (String line: new FileLineIterable(originalFile,false)) {
                int lastDelimiterStart = line.lastIndexOf(COLON_DELIMTER);
                if(lastDelimiterStart < 0){
                    throw new IOException("Unexpected input format on line: " + line);
                }
                String subLine = line.substring(0,lastDelimiterStart);
                String convertedLine = COLON_DELIMITER_PATTERN.matcher(subLine).replaceAll(",");
                writer.write(convertedLine);
                writer.write("\n");
                writer.flush();
            }
        }catch (IOException e){
            resultFile.delete();
            throw e;
        }
        return resultFile;
    }

    public static File readResourceToTempFile(String resourceName) throws IOException {
        InputSupplier<? extends  InputStream> inSupplier;
        try{
            URL resourceURL = Resources.getResource(GroupLensDataModel.class,resourceName);
            inSupplier = Resources.newInputStreamSupplier(resourceURL);
        }catch (IllegalArgumentException iae){
            File resourceFile = new File("src/main/java"+resourceName);
            inSupplier = Files.newInputStreamSupplier(resourceFile);
        }
        File tempFile = File.createTempFile("taste",null);
        tempFile.deleteOnExit();
        Files.copy(inSupplier,tempFile);
        return tempFile;
    }

    @Override
    public String toString() {
        return "GroupLensDataModel";
    }

}