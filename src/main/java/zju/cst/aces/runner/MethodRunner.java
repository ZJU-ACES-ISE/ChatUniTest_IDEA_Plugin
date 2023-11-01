package zju.cst.aces.runner;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import okhttp3.Response;
import zju.cst.aces.Windows.Panels.RegeneratePanel;
import zju.cst.aces.Windows.Panels.RepairPanel;
import zju.cst.aces.Windows.WindowConfig;
import zju.cst.aces.actions.MethodTestGeneration;
import zju.cst.aces.config.Config;
import zju.cst.aces.dto.Message;
import zju.cst.aces.dto.MethodInfo;
import zju.cst.aces.dto.PromptInfo;
import zju.cst.aces.util.*;
import zju.cst.aces.utils.LoggerUtil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MethodRunner extends ClassRunner {

    public MethodInfo methodInfo;

    public MethodRunner(String fullClassName, Config config, MethodInfo methodInfo) throws IOException {
        super(fullClassName, config);
        this.methodInfo = methodInfo;
    }

    @Override
    public void start() throws IOException {
        if (!config.isStopWhenSuccess() && config.isEnableMultithreading()) {
            ExecutorService executor = Executors.newFixedThreadPool(config.getTestNumber());
            List<Future<String>> futures = new ArrayList<>();
            for (int num = 1; num <= config.getTestNumber(); num++) {
                int finalNum = num;
                Callable<String> callable = new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        startRounds(finalNum);
                        return "";
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
        } else {
            //暂时禁用repair相关控制，都会repair
            for (int num = 1; num <= config.getTestNumber(); num++) {
                if (startRounds(num)) {
                    break;
                }
            }
            /*if (WindowConfig.regenerateReminder == false) {
                for (int num = 1; num <= config.getTestNumber(); num++) {
                    if (num == 1) {
                        if (startRounds(num)) {
                            break;
                        }
                    } else if (num > 1) {
                        Integer regenerateRecord = config.getRegenerate_record();
                        if (regenerateRecord == 0) {
                            boolean regenerate = continueRegeneratePanel();
                            if (regenerate) {
                                if (startRounds(num)) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        } else if (regenerateRecord == 1) {
                            if (startRounds(num)) {
                                break;
                            }
                        } else if (regenerateRecord == 2) {
                            break;
                        }
                    }
                }
            } else {
                for (int num = 1; num <= config.getTestNumber(); num++) {
                    if (startRounds(num)) {
                        break;
                    }
                }
            }*/

        }
    }

    //todo:修复compileError时两个面板同时出现的bug
    public boolean startRounds(final int num) throws IOException {
        PromptInfo promptInfo = null;
        String testName = className + separator + methodInfo.methodName + separator
                + classInfo.methodSignatures.get(methodInfo.methodSignature) + separator + num + separator + "Test";
        String fullTestName = fullClassName + separator + methodInfo.methodName + separator
                + classInfo.methodSignatures.get(methodInfo.methodSignature) + separator + num + separator + "Test";
        LoggerUtil.info(config.project, "[Generating] Generating test for method "
                + methodInfo.methodName + " round " + num );
        for (int rounds = 1; rounds <= config.getMaxRounds(); rounds++) {
            if (promptInfo == null) {
//                LoggerUtil.info(config.project, "Generating test for method  " + methodInfo.methodName + "  round " + rounds );
                if (methodInfo.dependentMethods.size() > 0) {
                    promptInfo = generatePromptInfoWithDep(classInfo, methodInfo);
                } else {
                    promptInfo = generatePromptInfoWithoutDep(classInfo, methodInfo);
                }
            } else {
                if(WindowConfig.notifyRepair==0){
                    LoggerUtil.info(config.project, ("[Repairing] Fixing test for method  " + methodInfo.methodName + "  ,round " + rounds ));
                }
                /*else if(WindowConfig.notifyRepair==1&&rounds==2){
                    LoggerUtil.info(config.project, ("[Repairing] Fixing test for method  "+ methodInfo.methodName));
                }*/
//                LoggerUtil.info(config.project, ("[Repairing] Fixing test for method  " + methodInfo.methodName + "  round " + rounds ));
            }
            List<Message> prompt = generateMessages(promptInfo);
//            config.getLog().debug("[Prompt]:\n" + prompt.toString());

            AskGPT askGPT = new AskGPT(config);
            Response response = askGPT.askChatGPT(prompt);
            Path savePath = testOutputPath.resolve(fullTestName.replace(".", File.separator) + ".java");

            String code = parseResponse(response);
            if (code.isEmpty()) {
                LoggerUtil.error(config.project, "Test for method  " + methodInfo.methodName + "  extract code failed");
                continue;
            }
            code = changeTestName(code, className, testName);
            code = repairPackage(code, classInfo.packageDeclaration);
            code = addTimeout(code, testTimeOut);
            promptInfo.setUnitTest(code); // Before repair imports
            code = repairImports(code, classInfo.imports);

            TestCompiler compiler = new TestCompiler(config, code);
            boolean compileResult = compiler.compileTest(testName,
                    errorOutputPath.resolve(testName + "_CompilationError_" + rounds + ".txt"), promptInfo, fullClassName);
            if (!compileResult) {
                //todo:修复repairround相关bug
                /*if(WindowConfig.notifyRepair==0){
                    LoggerUtil.info(config.project, "Test for method  " + methodInfo.methodName + "  compilation failed");
                }
                if (config.getRepair_record() == 0) {
                    if (!continueRepairPanel()) {
                        return true;
                    }
                } else if (config.getRepair_record() == 2) {
                    return true;
                }*/
                continue;
            }
            if (config.isNoExecution()) {
                exportTest(code, savePath);
                LoggerUtil.info(config.project, "Test for method  " + methodInfo.methodName + " generated successfully");
                return true;
            }
            if (compiler.executeTest(fullTestName, errorOutputPath.resolve(testName + "_ExecutionError_" + rounds + ".txt"), promptInfo)) {
                exportTest(code, savePath);
                LoggerUtil.info(config.project, "[Success]Test for method  " + methodInfo.methodName + "  generated successfully");
                MethodTestGeneration.recordInfo.setResultTest(code);
                return true;
            } else {
                if(WindowConfig.notifyRepair==0){
                    LoggerUtil.info(config.project, "Test for method  " + methodInfo.methodName + "  execution failed");
                }
                if(rounds==config.getMaxRounds()&&num==config.getTestNumber()){
                    LoggerUtil.warn(config.project, "Test for method  " + methodInfo.methodName + "  generate failed");
                }
                if(WindowConfig.repairReminder==true){
                    if (config.getRepair_record() == 0) {
                        if (!continueRepairPanel()) {
                            return true;
                        }
                    } else if (config.getRepair_record() == 2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean continueRegeneratePanel() {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<Integer>();
        ApplicationManager.getApplication().invokeLater(() -> {
            RegeneratePanel regeneratePanel = new RegeneratePanel();
            regeneratePanel.getOkButton().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("remember = " + regeneratePanel.getCheckBox1().isSelected());
                    if (regeneratePanel.getCheckBox1().isSelected()) {
                        config.setRegenerate_record(1);//标志着记录了第二个按钮，regenerate
                    }
                    regeneratePanel.dispose();
                    completableFuture.complete(1);
                }
            });
            regeneratePanel.getCancelButton().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (regeneratePanel.getCheckBox1().isSelected()) {
                        config.setRegenerate_record(2);//标志着记录了第二个按钮，stop
                    }
                    regeneratePanel.dispose();
                    completableFuture.complete(2);
                }
            });
            regeneratePanel.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    regeneratePanel.dispose();
                    completableFuture.complete(2);
                }
            });
            regeneratePanel.setVisible(true);
        });
        try {
            if (completableFuture.get() == 1) {
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean continueRepairPanel() {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        ApplicationManager.getApplication().invokeLater(() -> {
            RepairPanel repairPanel = new RepairPanel();
            repairPanel.getOkButton().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (repairPanel.getCheckBox1().isSelected()) {
                        config.setRepair_record(1);//标志着记录了第一个按钮，repair
                    }
                    repairPanel.dispose();
                    completableFuture.complete(1);
                }
            });
            repairPanel.getCancelButton().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (repairPanel.getCheckBox1().isSelected()) {
                        config.setRepair_record(2);//标志着记录了第二个按钮，cancel repair
                    }
                    repairPanel.dispose();
                    completableFuture.complete(2);
                }
            });
            repairPanel.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    repairPanel.dispose();
                    completableFuture.complete(2);
                }
            });
            repairPanel.setVisible(true);
        });
        try {
            if (completableFuture.get() == 1) {
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}