package zju.cst.aces.util;

import com.intellij.compiler.CompilerMessageImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.project.MavenProject;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherConfig;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import zju.cst.aces.config.Config;
import zju.cst.aces.dto.PromptInfo;
import zju.cst.aces.dto.TestMessage;

import javax.tools.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class TestCompiler {
    public static File srcTestFolder = new File("src" + File.separator + "test" + File.separator + "java");
    public static File backupFolder = new File("src" + File.separator + "backup");
    public static Config config;
    public String code;
    public static Boolean result = null;
    public static VirtualFile tempJavaFile;

    public TestCompiler(Config config) {
        this.config = config;
        this.code = "";
    }

    public TestCompiler(Config config, String code) {
        this.config = config;
        this.code = code;
    }

    public boolean executeTest(String fullTestName, Path outputPath, PromptInfo promptInfo) {
        File file = outputPath.toAbsolutePath().getParent().toFile();
        try {
            List<String> classpathElements = new ArrayList<>();
            classpathElements.addAll(config.getClassPaths());
            List<URL> urls = new ArrayList<>();
            for (String classpath : classpathElements) {
                URL url = new File(classpath).toURI().toURL();
                urls.add(url);
            }
            urls.add(file.toURI().toURL());
            ClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), getClass().getClassLoader());

            // Use the ServiceLoader API to load TestEngine implementations
            ServiceLoader<TestEngine> testEngineServiceLoader = ServiceLoader.load(TestEngine.class, classLoader);

            // Create a LauncherConfig with the TestEngines from the ServiceLoader
            LauncherConfig launcherConfig = LauncherConfig.builder()
                    .enableTestEngineAutoRegistration(false)
                    .enableTestExecutionListenerAutoRegistration(false)
                    .addTestEngines(testEngineServiceLoader.findFirst().orElseThrow())
                    .build();

            Launcher launcher = LauncherFactory.create(launcherConfig);

            // Register a listener to collect test execution results.
            SummaryGeneratingListener listener = new SummaryGeneratingListener();
            launcher.registerTestExecutionListeners(listener);

            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(selectClass(classLoader.loadClass(fullTestName)))
                    .build();
            launcher.execute(request);

            TestExecutionSummary summary = listener.getSummary();
            if (summary.getTestsFailedCount() > 0) {
                TestMessage testMessage = new TestMessage();
                List<String> errors = new ArrayList<>();
                summary.getFailures().forEach(failure -> {
                    for (StackTraceElement st : failure.getException().getStackTrace()) {
                        if (st.getClassName().equals(fullTestName)) {
                            errors.add(failure.getTestIdentifier().getDisplayName() + ": "
                                    + " line: " + st.getLineNumber() + " "
                                    + failure.getException().toString());
                        }
                    }
                });
                testMessage.setErrorType(TestMessage.ErrorType.RUNTIME_ERROR);
                testMessage.setErrorMessage(errors);
                promptInfo.setErrorMsg(testMessage);

                exportError(errors.toString(), outputPath);
            }
//            summary.printTo(new PrintWriter(System.out));
            return summary.getTestsFailedCount() == 0;
        } catch (Exception e) {
            throw new RuntimeException("In TestCompiler.executeTest: " + e);
        }
    }

    /**
     * Compile test file
     */
    public boolean compileTest(String className, Path outputPath, PromptInfo promptInfo, String fullClassName) {
        try {
            return getResult(className, outputPath, promptInfo, fullClassName);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean compileTest_using_CompilerManager(String className, Path outputPath, PromptInfo promptInfo) {
        return false;
    }

    public boolean getResult(String className, Path outputPath, PromptInfo promptInfo, String fullClassName) throws ExecutionException, InterruptedException {
        List<String> errorList = new ArrayList<>();
        Project project = config.getProject();
        VirtualFile tempDir = project.getBaseDir().findFileByRelativePath("/src/main/java");
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
                        result = false;
                        // If there are errors, collect the error messages
                        CompilerMessage[] errorMessages = compileContext.getMessages(CompilerMessageCategory.ERROR);
                        for (CompilerMessage errorMessage : errorMessages) {
                            int lineNumber = ((CompilerMessageImpl) errorMessage).getLine();
                            errorList.add("Error on Line " + lineNumber + " : " + errorMessage.getMessage());
                            System.out.println(errorMessage.getMessage());
                            TestMessage testMessage = new TestMessage();
                            testMessage.setErrorType(TestMessage.ErrorType.COMPILE_ERROR);
                            testMessage.setErrorMessage(errorList);
                            promptInfo.setErrorMsg(testMessage);
                            exportError(errorList.toString(), outputPath);
                        }
                        ApplicationManager.getApplication().invokeLater(()->{
                            ApplicationManager.getApplication().runWriteAction(() -> {
                                try {
                                    tempJavaFile.delete(this);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        });
                        compileFuture.complete(false);
                    } else {
                        result = true;
                        ApplicationManager.getApplication().invokeLater(()->{
                            ApplicationManager.getApplication().runWriteAction(() -> {
                                try {
                                    tempJavaFile.delete(this);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        },ModalityState.defaultModalityState());
                        // 设置CompletableFuture的结果，以便通知编译完成
                        compileFuture.complete(true);
                    }
                }
            });
        }, ModalityState.defaultModalityState());
        return compileFuture.get();
    }

    public void exportError(String error, Path outputPath) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()));
            writer.write(code);
            writer.write(error);
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException("In TestCompiler.exportError: " + e);
        }
    }

    public static List<String> listClassPaths(MavenProject mavenProject) {
        List<String> classPaths = new ArrayList<>();
        for (MavenArtifact dependency : mavenProject.getDependencies()) {
            classPaths.add(dependency.getPath());
        }
        String outputDirectory = mavenProject.getOutputDirectory();
        classPaths.add(outputDirectory);
        return classPaths;
    }

    /**
     * Move the src/test/java folder to a backup folder
     */
    public void copyAndBackupTestFolder() {
        restoreTestFolder();
        if (srcTestFolder.exists()) {
            try {
                FileUtils.copyDirectoryStructure(srcTestFolder, backupFolder);
                FileUtils.deleteDirectory(srcTestFolder);
                FileUtils.copyDirectoryStructure(config.getTestOutput().toFile(), srcTestFolder);
            } catch (IOException e) {
                throw new RuntimeException("In TestCompiler.copyAndBackupTestFolder: " + e);
            }
        }
    }

    /**
     * Restore the backup folder to src/test/java
     */
    public void restoreTestFolder() {
        if (backupFolder.exists()) {
            try {
                if (srcTestFolder.exists()) {
                    FileUtils.deleteDirectory(srcTestFolder);
                }
                FileUtils.copyDirectoryStructure(backupFolder, srcTestFolder);
                FileUtils.deleteDirectory(backupFolder);
            } catch (IOException e) {
                throw new RuntimeException("In TestCompiler.restoreTestFolder: " + e);
            }
        }
    }
}
