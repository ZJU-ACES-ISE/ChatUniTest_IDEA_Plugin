package zju.cst.aces.IDEA.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateGitignoreUtil {
    static String[] rule_array = {
            /*"chatunitest/",
            "tmp/",
            "src/test/java/*_*_*_*_*.java",
            "src/main/java/*_*_*_*_*.java"*/
            "*",
            "!.gitignore"
    };
//todo:修改添加gitignore以使得适配多模块的项目
    public static void addToFile(File gitignoreFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(gitignoreFile));
            List<String> lines = new ArrayList<>();
            boolean rulesFound = true;

            // 读取文件内容并检查是否存在规则
            String line;
            for (String rule : rule_array) {
                boolean ruleFound = false;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                    if (line.equals(rule)) {
                        ruleFound = true;
                        break;
                    }
                }
                rulesFound = rulesFound && ruleFound;
                reader.close();
                reader = new BufferedReader(new FileReader(gitignoreFile)); // 重新打开文件以检查下一个规则
            }

            // 如果规则不存在，则添加规则
            if (!rulesFound) {
                FileWriter writer = new FileWriter(gitignoreFile, true);
                for (String rule : rule_array) {
                    if (!lines.contains(rule)) {
                        writer.write(rule + System.lineSeparator());
                    }
                }
                writer.close();
            } else {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeFromFile(File gitignoreFile){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(gitignoreFile));
            List<String> lines = new ArrayList<>();
            boolean rulesFound = false;

            // 读取文件内容并检查是否存在规则
            String line;
            while ((line = reader.readLine()) != null) {
                boolean ruleFound = false;
                for (String rule : rule_array) {
                    if (line.equals(rule)) {
                        ruleFound = true;
                        break;
                    }
                }
                if (!ruleFound) {
                    lines.add(line);
                } else {
                    rulesFound = true;
                }
            }
            reader.close();

            // 如果找到规则，将新内容写回文件
            if (rulesFound) {
                FileWriter writer = new FileWriter(gitignoreFile);
                for (String updatedLine : lines) {
                    writer.write(updatedLine + System.lineSeparator());
                }
                writer.close();
            } else {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}