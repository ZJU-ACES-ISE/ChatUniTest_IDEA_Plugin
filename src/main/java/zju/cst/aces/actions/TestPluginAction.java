package zju.cst.aces.actions;

import com.intellij.compiler.CompilerManagerImpl;
import com.intellij.compiler.CompilerMessageImpl;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.testFramework.LightVirtualFile;
import org.jetbrains.annotations.Nullable;
import zju.cst.aces.config.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class TestPluginAction extends AnAction{
    Config config;
    @Override
    public void actionPerformed(AnActionEvent event) {
        String code="package com.hhh.plugin;\n" +
                "import org.junit.jupiter.api.Timeout;\n" +
                "import org.junit.jupiter.api.Test;\n" +
                "import java.lang.reflect.Method;\n" +
                "import java.lang.reflect.InvocationTargetException;\n" +
                "\n" +
                "import static org.junit.jupiter.api.Assertions.assertEquals;\n" +
                "\n" +
                "public class MyTest_mytest01_0_1_Test {\n" +
                "\n" +
                "    @Test\n" +
                "    @Timeout(8000)\n" +
                "    public void testMytest01() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {\n" +
                "        MyTest myTest = new MyTest();\n" +
                "\n" +
                "        // Get the reference to the private method using reflection\n" +
                "        Method mytest01 = MyTest.class.getDeclaredMethod(\"mytest01\");\n" +
                "        mytest01.setAccessible(true);\n" +
                "\n" +
                "        // Invoke the private method\n" +
                "        mytest01.invoke(myTest);\n" +
                "\n" +
                "        // Verify the output\n" +
                "        // Here, we assume that the method prints \"hello plugin\" to the console\n" +
                "        // So, we capture the console output and compare it with the expected value\n" +
                "        // You can modify this assertion based on the actual behavior of the method\n" +
                "        assertEquals(\"hello plugin\\n\", getConsoleOutput());\n" +
                "    }\n" +
                "\n" +
                "    private String getConsoleOutput() {\n" +
                "        // Capture the console output\n" +
                "        // You can implement this method using any library or technique of your choice\n" +
                "        // Here, we assume that the output is captured and returned as a string\n" +
                "        // You can modify this implementation based on your requirements\n" +
                "        return \"\";\n" +
                "    }\n" +
                "}";

        CompileTest.getCompileResult(event.getProject(), code, "MyTest_mytest01_0_1_Test", errorList -> {
            if (errorList == null || errorList.isEmpty()) {
                System.out.println("no error");
                System.out.println("CompileTest.result = " + CompileTest.result);
            } else {
                System.out.println(errorList.toString());
                System.out.println("CompileTest.result = " + CompileTest.result);
            }
        });

    }

//    @Override
    public void actionPerformed1(AnActionEvent event) {
        Project project = event.getProject();





        CompilerManager compilerManager = CompilerManager.getInstance(project);

        // 获取 CompilerManager 实例

        /*VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(project.getBasePath() + "/src/main/java/com/hhh/plugin/MyTest.java");
        compileJavaFile(project,virtualFile);*/
        String code="package com.hhh.plugin;\n" +
                "import org.junit.jupite.api.Timeout;\n" +
                "import org.junit.jupiter.api.Test;\n" +
                "import java.lang.reflect.Method;\n" +
                "import java.lang.reflect.InvocationTargetException;\n" +
                "\n" +
                "import static org.junit.jupiter.api.Assertions.assertEquals;\n" +
                "\n" +
                "public class MyTest_mytest01_0_1_Test {\n" +
                "\n" +
                "    @Test\n" +
                "    @Timeout(8000)\n" +
                "    public void testMytest01() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {\n" +
                "        MyTest myTest = new MyTest();\n" +
                "\n" +
                "        // Get the reference to the private method using reflection\n" +
                "        Method mytest01 = MyTest.class.getDeclaredMethod(\"mytest01\");\n" +
                "        mytest01.setAccessible(true);\n" +
                "\n" +
                "        // Invoke the private method\n" +
                "        mytest01.invoke(myTest);\n" +
                "\n" +
                "        // Verify the output\n" +
                "        // Here, we assume that the method prints \"hello plugin\" to the console\n" +
                "        // So, we capture the console output and compare it with the expected value\n" +
                "        // You can modify this assertion based on the actual behavior of the method\n" +
                "        assertEquals(\"hello plugin\\n\", getConsoleOutput());\n" +
                "    }\n" +
                "\n" +
                "    private String getConsoleOutput() {\n" +
                "        // Capture the console output\n" +
                "        // You can implement this method using any library or technique of your choice\n" +
                "        // Here, we assume that the output is captured and returned as a string\n" +
                "        // You can modify this implementation based on your requirements\n" +
                "        return \"\";\n" +
                "    }\n" +
                "}";
//        String className="MyTest_mytest01_0_1_Test";
//        String path="/src/main/java/com/hhh/plugin/";
//        createVirtualFile(project, className, code);
//        VirtualFile virtualFile=LocalFileSystem.getInstance().findFileByIoFile(new File(project.getBasePath()+path+className+".java"));

        ApplicationManager.getApplication().runWriteAction(()->{
            createVirtualFile(event.getProject(),"MyTest_mytest01_0_1_Test",code);
            VirtualFile javaFile = LocalFileSystem.getInstance().findFileByPath(event.getProject().getBasePath() + "/src/main/java/codeCompile/" + "MyTest_mytest01_0_1_Test.java");
            compileJavaFile(event.getProject(),javaFile);
        });

    }
    public static void compileJavaFile(Project project, VirtualFile javaFile) {
        CompilerManagerImpl compilerManager = new CompilerManagerImpl(project);
        VirtualFile[] filesToCompile = new VirtualFile[]{javaFile};
        // Compiling the Java file
        compilerManager.compile(filesToCompile, new CompileStatusNotification() {
            @Override
            public void finished(boolean aborted, int errors, int warnings, CompileContext compileContext) {
                List<String> errorList=new ArrayList<>();
                // Compilation finished, you can access the results here
                if (errors > 0) {
                    CompilerMessage[] errorMessages = compileContext.getMessages(CompilerMessageCategory.ERROR);
                    for (CompilerMessage errorMessage : errorMessages) {
                        int lineNumber = ((CompilerMessageImpl) errorMessage).getLine();
                        errorList.add("Error on Line "+lineNumber+" : "+errorMessage.getMessage());
                    }
                }
                if (!aborted&&errors==0) {
                    System.out.println("Compilation finished successfully.");
                } else {
                    System.out.println(errorList.toString());
                    System.out.println("Compilation aborted.");
                }
            }
        });
    }
//todo 打包插件试下能不能使用idea内置的编译器
    //todo 目前策略:抽取文件->编译文件temp->若成功->.class文件放在指定位置->删除target下这个.class       若失败->直接删除temp文件和.class文件

    public static void createVirtualFile(Project project, String className, String code) {
        String fileName = className + ".java";
        PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);

        PsiFile psiFile = ApplicationManager.getApplication().runWriteAction((Computable<PsiFile>) () -> {
            PsiFile psiFile1 = psiFileFactory.createFileFromText(fileName, JavaFileType.INSTANCE, code);
            PsiManager psiManager = PsiManager.getInstance(project);
            VirtualFile baseDir = project.getBaseDir();
            VirtualFile srcMainDir = baseDir.findFileByRelativePath("/src/main/java");
            try {
                VirtualFile compileCodeDir = LocalFileSystem.getInstance().findFileByPath(String.valueOf(Paths.get(project.getBasePath(),"/src/main/java/codeCompile")));
                if(compileCodeDir==null){
                    compileCodeDir = srcMainDir.createChildDirectory(null, "codeCompile");
                }
                PsiJavaDirectoryImpl psiJavaDirectory = (PsiJavaDirectoryImpl) psiManager.findDirectory(compileCodeDir);
                psiJavaDirectory.add(psiFile1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return psiFile1;
        });
    }


}
