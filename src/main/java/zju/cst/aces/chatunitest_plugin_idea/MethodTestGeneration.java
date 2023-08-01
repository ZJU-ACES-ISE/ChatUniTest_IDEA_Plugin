package zju.cst.aces.chatunitest_plugin_idea;

import zju.cst.aces.parser.ProjectParser;
import zju.cst.aces.utils.FileUtil;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import zju.cst.aces.config.Config;
import zju.cst.aces.dto.ClassInfo;
import zju.cst.aces.dto.MethodInfo;
import zju.cst.aces.runner.ClassRunner;
import zju.cst.aces.runner.MethodRunner;
import zju.cst.aces.utils.LoggerUtil;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class MethodTestGeneration {
    public static void generate_method_test_old(Config config, String fullClassName, String methodName) {
        Project project = config.project;
        ProjectParser parser = new ProjectParser(config);
        parser.parse();
        LoggerUtil.info(project, "[ChatTester] Parse finished");
        LoggerUtil.info(project, "[ChatTester] Generating tests for project: " + project.getName());
        //todo：消息弹出顺序不对
        //todo:用户得可以中断生成，要不然一直跑
            try {
                LoggerUtil.info(project, "[ChatTester] Generating tests for class:  " + fullClassName
                        + ", method: " + methodName);
                ClassRunner classRunner = new ClassRunner(fullClassName, config);
                ClassInfo classInfo = classRunner.classInfo;
                MethodInfo methodInfo = null;
                for (String mSig : classInfo.methodSignatures.keySet()) {
                    if (mSig.split("\\(")[0].equals(methodName)) {
                        methodInfo = classRunner.getMethodInfo(classInfo, mSig);
                        break;
                    }
                }
                if (methodInfo == null) {
                    throw new RuntimeException("Method " + methodName + " in class " + fullClassName + " not found");
                }
                MethodInfo finalMethodInfo = methodInfo;
                try {
                    new MethodRunner(fullClassName, config, finalMethodInfo).start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                throw new RuntimeException("In MethodTestMojo.execute: " + e);
            }
            LoggerUtil.info(project, "[ChatTester] Generation finished");
            FileUtil.refreshFolder(config.testOutput);
    }

    public static void generate_method_test(Config config, String fullClassName, String methodName) {
        Project project = config.project;
        CompletableFuture<Void> classTask=CompletableFuture.runAsync(()->{
            ProjectParser parser = new ProjectParser(config);
            parser.parse();
            LoggerUtil.info(project, "[ChatTester] Parse finished");
            LoggerUtil.info(project, "[ChatTester] Generating tests for project: " + project.getName());
            //todo：消息弹出顺序不对
            //todo:用户得可以中断生成，要不然一直跑
            try {
                LoggerUtil.info(project, "[ChatTester] Generating tests for class:  " + fullClassName
                        + ", method: " + methodName);
                ClassRunner classRunner = new ClassRunner(fullClassName, config);
                ClassInfo classInfo = classRunner.classInfo;
                MethodInfo methodInfo = null;
                for (String mSig : classInfo.methodSignatures.keySet()) {
                    if (mSig.split("\\(")[0].equals(methodName)) {
                        methodInfo = classRunner.getMethodInfo(classInfo, mSig);
                        break;
                    }
                }
                if (methodInfo == null) {
                    throw new RuntimeException("Method " + methodName + " in class " + fullClassName + " not found");
                }
                MethodInfo finalMethodInfo = methodInfo;
                try {
                    new MethodRunner(fullClassName, config, finalMethodInfo).start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                throw new RuntimeException("In MethodTestMojo.execute: " + e);
            }
            LoggerUtil.info(project, "[ChatTester] Generation finished");
            FileUtil.refreshFolder(config.testOutput);
        });
    }

}
