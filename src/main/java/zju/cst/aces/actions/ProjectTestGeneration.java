package zju.cst.aces.actions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.application.ApplicationManager;
import zju.cst.aces.utils.FileUtil;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;
import zju.cst.aces.config.Config;
import zju.cst.aces.parser.ProjectParser;
import zju.cst.aces.runner.ClassRunner;
import zju.cst.aces.utils.LoggerUtil;
import zju.cst.aces.utils.UpdateGitignoreUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ProjectTestGeneration {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static void generate_project_test(Config config) {
        ApplicationManager.getApplication().executeOnPooledThread(()->{
            Project project = config.project;
            String basePath = config.basePath;
            CompletableFuture<Void> classTask=CompletableFuture.runAsync(()-> {

                ProjectParser parser = new ProjectParser(config);
                parser.parse();
                LoggerUtil.info(project, "[ChatUniTest] Generating tests for project: " + project.getName());
//                LoggerUtil.warn(project, "[ChatUniTest] It may consume a significant number of tokens!");

                Path srcMainJavaPath = Paths.get(basePath, "src", "main", "java");
                if (!srcMainJavaPath.toFile().exists()) {
                    LoggerUtil.error(project, "[ChatUniTest] No compile source found in " + project);
                    return;
                }


                List<String> classPaths = new ArrayList<>();
                parser.scanSourceDirectory(srcMainJavaPath.toFile(), classPaths);
                if (config.isEnableMultithreading() == true) {
                    classJob(classPaths, config);
                } else {
                    for (String classPath : classPaths) {
                        String className = classPath.substring(classPath.lastIndexOf(File.separator) + 1, classPath.lastIndexOf("."));
                        try {
                            className = getFullClassName(className, config);
                            String finalClassName = className;
                            ApplicationManager.getApplication().invokeLater(() -> {
                                LoggerUtil.info(project, "[ChatUniTest] Generating tests for class: " + getSimpleClassName(finalClassName));
                            });
                            new ClassRunner(className, config).start();
                        } catch (IOException e) {
                            String finalClassName1 = className;
                            ApplicationManager.getApplication().invokeLater(() -> {
                                LoggerUtil.error(project, "[ChatUniTest] Generate tests for class " + getSimpleClassName(finalClassName1) + " failed" );
                            });
                        }
                    }
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
        });

    }

    public static void classJob(List<String> classPaths, Config config) {
        ExecutorService executor = Executors.newFixedThreadPool(config.getClassThreads());
        List<Future<String>> futures = new ArrayList<>();
        for (String classPath : classPaths) {
            Callable<String> callable = new Callable<String>() {
                @Override
                public String call() throws Exception {
                    String className = classPath.substring(classPath.lastIndexOf(File.separator) + 1, classPath.lastIndexOf("."));
                    try {
                        className = getFullClassName(className, config);
                        LoggerUtil.info(config.project, "[ChatTester] Generating tests for class  " + getSimpleClassName(className));
                        new ClassRunner(className, config).start();
                    } catch (IOException e) {
                        LoggerUtil.error(config.project, "[ChatTester] Generate tests for class " + getSimpleClassName(className) + " failed ");
                    }
                    return "Processed " + classPath;
                }
            };
            Future<String> future = executor.submit(callable);
            futures.add(future);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                executor.shutdownNow();
            }
        });

        for (Future<String> future : futures) {
            try {
                String result = future.get();
                System.out.println(result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }


    public static String getFullClassName(String name, Config config) throws IOException {
        if (isFullName(name)) {
            return name;
        }
        Path classMapPath = config.getClassMapPath();
        Map<String, List<String>> classMap = GSON.fromJson(Files.readString(classMapPath, StandardCharsets.UTF_8), Map.class);
        if (classMap.containsKey(name)) {
            if (classMap.get(name).size() > 1) {
                throw new RuntimeException("[ChatTester] Multiple classes Named " + name + ": " + classMap.get(name)
                        + " Please use full qualified name!");
            }
            return classMap.get(name).get(0);
        }
        return name;
    }

    public static boolean isFullName(String name) {
        if (name.contains(".")) {
            return true;
        }
        return false;
    }

    public static String getSimpleClassName(String fullClassName){
        int lastIndexOfDot = fullClassName.lastIndexOf(".");
        if(lastIndexOfDot==-1){
            return fullClassName;
        }
        else {
            return fullClassName.substring(lastIndexOfDot+1);
        }
    }
}

