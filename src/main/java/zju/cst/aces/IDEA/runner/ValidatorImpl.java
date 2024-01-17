package zju.cst.aces.IDEA.runner;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.intellij.compiler.CompilerMessageImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.project.Project;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import zju.cst.aces.actions.ChatunitestPluginAction;
import zju.cst.aces.api.Validator;
import zju.cst.aces.api.config.Config;
import zju.cst.aces.dto.PromptInfo;
import zju.cst.aces.dto.TestMessage;
import zju.cst.aces.util.TestCompiler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

//验证器（测试编译结果，执行结果）
public class ValidatorImpl implements Validator {
    TestCompiler compiler;
    static VirtualFile tempJavaFile;
    zju.cst.aces.api.Project project_impl;

    public ValidatorImpl(Path testOutputPath, Path compileOutputPath, Path targetPath, List<String> classpathElements, zju.cst.aces.api.Project project) {
        this.compiler = new TestCompiler(testOutputPath, compileOutputPath, targetPath, classpathElements);
        this.project_impl=project;
    }

    @Override
    public boolean syntacticValidate(String code) {
        try {
            StaticJavaParser.parse(code);
            return true;
        } catch (ParseProblemException e) {
            return false;
        }
    }

    @Override
    public boolean semanticValidate(String code, String className, Path outputPath, PromptInfo promptInfo) {
        setCode(code);
        try {
            return compileTest(className,outputPath,promptInfo);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean runtimeValidate(String fullTestName) {
        return compiler.executeTest(fullTestName).getTestsFailedCount() == 0;
    }

    public void setCode(String code) {
        compiler.setCode(code);
    }

    //idea插件仅需要改compile方法
    @Override
    public boolean compile(String className, Path outputPath, PromptInfo promptInfo) {
        try {
            return compileTest(className,outputPath,promptInfo);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TestExecutionSummary execute(String fullTestName) {
        return compiler.executeTest(fullTestName);
    }

    public void exportError(List<String> errors, Path outputPath ,String code) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()));
            writer.write(code);
            writer.write("\n--------------------------------------------\n");
            writer.write(errors.stream().collect(Collectors.joining("\n")));
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException("In TestCompiler.exportError: " + e);
        }
    }

    public boolean compileTest(String className, Path outputPath, PromptInfo promptInfo) throws ExecutionException, InterruptedException {
        String code=compiler.code;
        Project project= ChatunitestPluginAction.IDEAProject;
        VirtualFile tempDir = LocalFileSystem.getInstance().findFileByIoFile(Paths.get(String.valueOf(project_impl.getBasedir()), "src", "test", "java").toFile());
        List<String> errorList = new ArrayList<>();
        CompletableFuture<Boolean> compileFuture = new CompletableFuture<>();
        // Create a temporary Java file
        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationManager.getApplication().runWriteAction(() -> {
                try {
                    tempJavaFile = tempDir.createChildData(null, className + ".java");
                    tempJavaFile.setBinaryContent(code.getBytes());
                    if (!outputPath.toAbsolutePath().getParent().toFile().exists()) {
                        outputPath.toAbsolutePath().getParent().toFile().mkdirs();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            CompilerManager compilerManager = CompilerManager.getInstance(project);
            VirtualFile[] filesToCompile = new VirtualFile[]{tempJavaFile};
            // 创建CompletableFuture对象来处理编译结果的回调
            compilerManager.compile(filesToCompile, new CompileStatusNotification() {
                @Override
                public void finished(boolean aborted, int errors, int warnings, CompileContext compileContext) {
                    if (errors > 0) {
                        // If there are errors, collect the error messages
                        CompilerMessage[] errorMessages = compileContext.getMessages(CompilerMessageCategory.ERROR);
                        for (CompilerMessage errorMessage : errorMessages) {
                            int lineNumber = ((CompilerMessageImpl) errorMessage).getLine();
                            errorList.add("Error on Line " + lineNumber + " : " + errorMessage.getMessage());
                            TestMessage testMessage = new TestMessage();
                            testMessage.setErrorType(TestMessage.ErrorType.COMPILE_ERROR);
                            testMessage.setErrorMessage(errorList);
                            promptInfo.setErrorMsg(testMessage);
                            exportError(errorList, outputPath,code);
                        }
                        ApplicationManager.getApplication().invokeLater(()->{
                            ApplicationManager.getApplication().runWriteAction(() -> {
                                //将tempFile添加至列表，最后删除
                                ChatunitestPluginAction.tempTestJavaFiles.add(tempJavaFile);
                                /*try {
                                    //最后删除文件
                                    tempJavaFile.delete(this);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }*/
                            });
                        });
                        compileFuture.complete(false);
                    } else {
                        ApplicationManager.getApplication().invokeLater(()->{
                            ApplicationManager.getApplication().runWriteAction(() -> {
                                ChatunitestPluginAction.tempTestJavaFiles.add(tempJavaFile);
                                /*try {
                                    tempJavaFile.delete(this);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }*/
                            });
                        }, ModalityState.defaultModalityState());
                        // 设置CompletableFuture的结果，以便通知编译完成
                        compileFuture.complete(true);
                    }
                }
            });
        }, ModalityState.defaultModalityState());
        // 使用thenAccept来异步处理结果
        return compileFuture.get();
    }
}
