package zju.cst.aces.utils;

import zju.cst.aces.dto.RecordInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProcessRecordUtil {
    public static void recordProcess(String basePath,String output,String fullClassName,String methodName,RecordInfo recordInfo){
        Path recordFilePath = Paths.get(basePath, output, fullClassName.replaceAll("\\.", "/"));
        File directory = new File(recordFilePath.toFile().getPath());
        if(!directory.exists()){
            directory.mkdirs();
        }
        long timestamp = System.currentTimeMillis();
        File file = new File(directory, methodName+"-"+timestamp+".txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String result=String.format("{\n"+"\"createTime\":\"%s\",\n\n"+
                "\"methodInfo\":\n\"%s\",\n\n"+
                "\"specification\":\n\"%s\",\n\n"+
                "\"prompt\":\n\"%s\",\n\n"+
                "\"resultTest\n\":\"%s\"\n"+"}",recordInfo.getCreateTime(),recordInfo.getMethodInfo(),
                recordInfo.getSpecification(),recordInfo.getPrompt(),recordInfo.getResultTest());
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(result);
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
