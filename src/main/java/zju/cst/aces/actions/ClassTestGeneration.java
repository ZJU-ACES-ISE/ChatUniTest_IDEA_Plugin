package zju.cst.aces.actions;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import zju.cst.aces.utils.FileUtil;
import com.intellij.openapi.project.Project;
import zju.cst.aces.config.Config;
import zju.cst.aces.parser.ProjectParser;
import zju.cst.aces.runner.ClassRunner;
import zju.cst.aces.utils.LoggerUtil;
import zju.cst.aces.utils.UpdateGitignoreUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class ClassTestGeneration {

    public static void generate_class_test(Config config, String fullClassName,String simpleClassName) {
        ApplicationManager.getApplication().executeOnPooledThread(()->{
            Project project = config.project;
            /*ProjectParser parser = new ProjectParser(config);
            parser.parse();
            ApplicationManager.getApplication().invokeLater(()->{
                LoggerUtil.info(project, "[ChatUniTest] Parse finished");

            });*/
            ApplicationManager.getApplication().invokeLater(()->{
                LoggerUtil.info(project, "[ChatUniTest] Generating tests for project: " + project.getName()+", class: "+simpleClassName);
            });
            try {
                new ClassRunner(fullClassName, config).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ApplicationManager.getApplication().invokeLater(()->{
                ApplicationManager.getApplication().runWriteAction(()->{
                    Path path = Paths.get(project.getBasePath(), ".gitignore");
                    File file = path.toFile();
                    //没有gitignore则无需处理
                    if(file.exists()){
                        UpdateGitignoreUtil.removeFromFile(file);
                    }
                });
                LoggerUtil.info(project, "[ChatUniTest] Generation finished");
            });
            FileUtil.refreshFolder(config.testOutput);
        });

    }
}
