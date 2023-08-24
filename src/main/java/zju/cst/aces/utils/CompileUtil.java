package zju.cst.aces.utils;

import com.intellij.compiler.CompilerManagerImpl;
import com.intellij.compiler.CompilerMessageImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import zju.cst.aces.dto.TestMessage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/*
 * 输出：code,classname,outputpath,
 * 返回编译结果：true/false,在编译结束后将报错信息存入outputpath对应的txt文件，同时移动编译结果文件
 * */
public class CompileUtil {
    public static Boolean result;
    public static VirtualFile tempJavaFile;

    public static Boolean compileCode(Project project, String code, String classname, Path outputPath) throws ExecutionException, InterruptedException {
//            VirtualFile tempDir = VirtualFileManager.getInstance().refreshAndFindFileByUrl("temp:///"); // 临时目录(行不通，编译永远通过)

        VirtualFile tempDir = project.getBaseDir().findFileByRelativePath("/src/main/java");

        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                tempJavaFile = tempDir.createChildData(null, classname + ".java");
                tempJavaFile.setBinaryContent(code.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        CompilerManagerImpl compilerManager = new CompilerManagerImpl(project);
        VirtualFile[] filesToCompile = new VirtualFile[]{tempJavaFile};
        // Compiling the Java file
        compilerManager.compile(filesToCompile, new CompileStatusNotification() {
            @Override
            public void finished(boolean aborted, int errors, int warnings, CompileContext compileContext) {
                List<String> errorList = new ArrayList<>();
                // Compilation finished, you can access the results here
                if (errors > 0) {
                    CompilerMessage[] errorMessages = compileContext.getMessages(CompilerMessageCategory.ERROR);
                    for (CompilerMessage errorMessage : errorMessages) {
                        int lineNumber = ((CompilerMessageImpl) errorMessage).getLine();
                        System.out.println(lineNumber);
                        errorList.add("Error on Line " + lineNumber + " : " + errorMessage.getMessage());
                    }
                }
                if (!aborted && errors == 0) {
                    result = true;
                    System.out.println("Compilation finished successfully.");
                } else {
                    result = false;
                    TestMessage testMessage = new TestMessage();
                    testMessage.setErrorType(TestMessage.ErrorType.COMPILE_ERROR);
                    testMessage.setErrorMessage(errorList);
                    exportError(errorList.toString(), outputPath, code);
                    System.out.println("Compilation aborted.");
                }
            }
        });
        return result;
    }



    public static void exportError(String error, Path outputPath, String code) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()));
            writer.write(code);
            writer.write(error);
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException("In TestCompiler.exportError: " + e);
        }
    }


}
