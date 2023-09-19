package zju.cst.aces.actions;

import com.fasterxml.jackson.jr.ob.JSON;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import zju.cst.aces.Windows.WindowConfig;
import zju.cst.aces.dto.Message;
import zju.cst.aces.parser.ProjectParser;
import zju.cst.aces.runner.AbstractRunner;
import zju.cst.aces.util.AskGPT;
import zju.cst.aces.util.CodeExtractor;
import zju.cst.aces.utils.FileUtil;
import com.intellij.openapi.project.Project;
import zju.cst.aces.config.Config;
import zju.cst.aces.dto.ClassInfo;
import zju.cst.aces.dto.MethodInfo;
import zju.cst.aces.runner.ClassRunner;
import zju.cst.aces.runner.MethodRunner;
import zju.cst.aces.utils.LoggerUtil;
import zju.cst.aces.utils.UpdateGitignoreUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static zju.cst.aces.runner.AbstractRunner.GSON;

public class MethodTestGeneration {
    public static void generate_method_test(Config config, String fullClassName, String simpleClassName,
                                            String methodName, String methodBody, PsiImportList importList) {
        ApplicationManager.getApplication().executeOnPooledThread(()->{
            CompletableFuture<Boolean> specificationFuture = new CompletableFuture<>();
            if(WindowConfig.test_specification){
                ApplicationManager.getApplication().invokeLater(()->{
                    //test specification部分
                    String input_code="";
                    if(importList!=null){
                        for (PsiImportStatement importStatement : importList.getImportStatements()) {
                            String importText=importStatement.getText();
                            input_code+=importText+"\n";
                        }
                    }
                    input_code+=methodBody;
                    AskGPT askGPT = new AskGPT(config);
                    ArrayList<Message> messages = new ArrayList<>();
                    Message message = new Message();
                    message.setContent("Please evaluate if a unit test is essential for the given method. if you decide it's necessary," +
                            "generate the unit test specifications in JSON format, which should include a list of test caseswith their names. purposes, and input data, if a unit test is not required, please respond with'No need to use test specification' The Java source code is:\n" +
                            "\"\"\" \n" +
                            input_code +
                            "\"\"\",return pure JSON code only.");
                    message.setRole("user");
                    messages.add(message);
                    Response response = askGPT.askChatGPT(messages);
                    CodeInputDialog specificationDialog = new CodeInputDialog();
                    specificationDialog.setTitle("Test Specification");
                    String test_specification= getSpecificationJson(response);
                    specificationDialog.setCodeInput(test_specification);
                    specificationDialog.show();
                    if (specificationDialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
                        String specificationCode = specificationDialog.getCodeInput();
                        //用户删除了所有内容，或者gpt提示不需要specification
                        if("".equals(StringUtils.deleteWhitespace(specificationCode))||specificationCode.toLowerCase().contains("no need")){
                            config.setUse_specification(false);
                        }
                        else {
                            config.setUse_specification(true);
                            config.setSpecification_code(specificationCode);
                        }
                    }else {
                        config.setUse_specification(false);
                    }
                    specificationFuture.complete(true);
                });
            }
            try {
                specificationFuture.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            //正常执行
            Project project = config.project;
            ApplicationManager.getApplication().invokeLater(()->{
                LoggerUtil.info(project, "[ChatUniTest] Generating tests for project: " + project.getName());
            });
            try {
                ApplicationManager.getApplication().invokeLater(()->{
                    LoggerUtil.info(project, "[ChatUniTest] Generating tests for class:  " + simpleClassName
                            + ", method: " + methodName);
                });
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
    public static String parseResponse(Response response) {
        if (response == null) {
            return "";
        }
        Map<String, Object> body = GSON.fromJson(response.body().charStream(), Map.class);
        String content = ((Map<String, String>) ((Map<String, Object>) ((ArrayList<?>) body.get("choices")).get(0)).get("message")).get("content");
        return new CodeExtractor(content).getExtractedCode();
    }
    public static String getSpecificationJson(Response response){
        Map<String, Object> body = GSON.fromJson(response.body().charStream(), Map.class);
        String test_specification = ((Map<String, String>) ((Map<String, Object>) ((ArrayList<?>) body.get("choices")).get(0)).get("message")).get("content");
        int startIndex=test_specification.indexOf("{");
        int endIndex=test_specification.lastIndexOf("}");
        if (startIndex != -1 && endIndex != -1) {
            // 现在 jsonContent 包含了纯净的 JSON 代码
            test_specification = test_specification.substring(startIndex, endIndex + 1);
        }
        return test_specification;
    }
}
