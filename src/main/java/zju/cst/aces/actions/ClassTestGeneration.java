package zju.cst.aces.actions;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import zju.cst.aces.utils.FileUtil;
import com.intellij.openapi.project.Project;
import zju.cst.aces.config.Config;
import zju.cst.aces.parser.ProjectParser;
import zju.cst.aces.runner.ClassRunner;
import zju.cst.aces.utils.LoggerUtil;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ClassTestGeneration {

    public static void generate_class_test(Config config, String fullClassName) {
        ApplicationManager.getApplication().executeOnPooledThread(()->{
            Project project = config.project;
            ProjectParser parser = new ProjectParser(config);
            parser.parse();
            ApplicationManager.getApplication().invokeLater(()->{
                LoggerUtil.info(project, "[ChatTester] Parse finished");

            });
            ApplicationManager.getApplication().invokeLater(()->{
                LoggerUtil.info(project, "[ChatTester] Generating tests for project: " + project.getName());
            });
            try {
                new ClassRunner(fullClassName, config).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ApplicationManager.getApplication().invokeLater(()->{
                LoggerUtil.info(project, "[ChatTester] Generation finished");
            });
            FileUtil.refreshFolder(config.testOutput);
        });

    }
}
