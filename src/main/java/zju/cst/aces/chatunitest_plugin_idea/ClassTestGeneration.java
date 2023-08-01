package zju.cst.aces.chatunitest_plugin_idea;

import zju.cst.aces.utils.FileUtil;
import com.intellij.openapi.project.Project;
import zju.cst.aces.config.Config;
import zju.cst.aces.parser.ProjectParser;
import zju.cst.aces.runner.ClassRunner;
import zju.cst.aces.utils.LoggerUtil;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClassTestGeneration {

    public static void generate_class_test(Config config, String fullClassName) {
        //todo:parser移到最外面一层
        //todo:是否需要自动编译一遍项目
        Project project = config.project;
        CompletableFuture<Void> classRunnerTask = CompletableFuture.runAsync(() -> {
            ProjectParser parser = new ProjectParser(config);
            parser.parse();
            LoggerUtil.info(project, "[ChatTester] Parse finished");
            LoggerUtil.info(project, "[ChatTester] Generating tests for project: " + project.getName());
            try {
                new ClassRunner(fullClassName, config).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            LoggerUtil.info(project, "[ChatTester] Generation finished");
            FileUtil.refreshFolder(config.testOutput);
        });
    }
}
