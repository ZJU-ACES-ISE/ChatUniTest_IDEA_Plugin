package zju.cst.aces.actions;

import zju.cst.aces.Windows.WindowConfig;
import zju.cst.aces.Windows.WindowDefaultConfig;
import zju.cst.aces.utils.ConnectUtil;
import zju.cst.aces.utils.JudgeUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;
import zju.cst.aces.config.Config;
import zju.cst.aces.parser.ProjectParser;
import zju.cst.aces.utils.LoggerUtil;

import java.nio.file.Paths;

public class ChatUniTestPluginAction extends AnAction {
    Config config;

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        String basePath = project.getBasePath();
        MavenProject mavenProject = MavenActionUtil.getMavenProject(event.getDataContext());
        if (JudgeUtil.isMavenProject(project)) {
            Messages.showMessageDialog("Please use maven-archetype project", "Error", Messages.getErrorIcon());
            return;
        }
        if (WindowConfig.apiKeys == null) {
            Messages.showMessageDialog("Please set apikey first", "Error", Messages.getErrorIcon());
            return;
        }
        init(project,basePath,mavenProject);
        if(!ConnectUtil.TestOpenApiConnection(config.getRandomKey(),WindowConfig.hostname,WindowConfig.port)){
            Messages.showMessageDialog("Please set apikey first", "Error", Messages.getErrorIcon());
        }

        /*VirtualFile virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        Application application = ApplicationManager.getApplication();
        config.application=application;
        //parse the project
        ProjectParser parser = new ProjectParser(config);
        application.invokeLater(()->{
            LoggerUtil.info(project, "[ChatTester] Parsing class info");
            parser.parse();
            LoggerUtil.info(project, "[ChatTester] Project parse finished");
        });
        *//*CompletableFuture<Void> classRunnerTask = CompletableFuture.runAsync(() -> {
            parser.parse();
        });
        try {
            classRunnerTask.get();
            LoggerUtil.info(project, "[ChatTester] Project parse finished");
        } catch (InterruptedException | ExecutionException e) {
            LoggerUtil.info(project, "[ChatTester] Project parse failed");
            throw new RuntimeException(e);
        }*//*
        if (basePath.equals(virtualFile.getPath())) {
            ProjectTestGeneration.generate_project_test(config);
            return;
        } else if (virtualFile.getFileType() == FileTypeManager.getInstance().getFileTypeByExtension("java") && !isMouseOnMethod(event)) {
            PsiJavaFile psiJavaFile = (PsiJavaFile) event.getData(CommonDataKeys.PSI_FILE);
            String fullClassName = String.format("%s.%s", psiJavaFile.getPackageName(), virtualFile.getNameWithoutExtension());
            ClassTestGeneration.generate_class_test(config,fullClassName);
            return;
        } else if (isMouseOnMethod(event)) {
            PsiJavaFile psiJavaFile = (PsiJavaFile) event.getData(CommonDataKeys.PSI_FILE);
            String fullClassName = String.format("%s.%s", psiJavaFile.getPackageName(), virtualFile.getNameWithoutExtension());
            String methodName=JudgeUtil.getMethodName(event);
            MethodTestGeneration.generate_method_test(config,fullClassName,methodName);
            return;
        }*/
    }

    /*插件初始化时，设置插件按钮的可见性*/
    @Override
    public void update(AnActionEvent event) {
        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        Project project = event.getProject();
        //是否是对应路径的java文件
        boolean isJavaFile = file != null && FileTypeRegistry.getInstance().isFileOfType(file, com.intellij.openapi.fileTypes.StdFileTypes.JAVA);
        boolean isInJavaSource = project != null && file != null && file.getPath().startsWith(project.getBasePath() + "/src/main/java");
        // 检查所选项目目录是否满足条件
        boolean isRootProject = project != null && file != null && project.getBasePath().equals(file.getPath());
        if ((isJavaFile && isInJavaSource) || isRootProject) {
            // 启用或禁用按钮并设置可见性
            event.getPresentation().setEnabledAndVisible(true);

        } else {
            event.getPresentation().setEnabledAndVisible(false);
        }
        PsiElement psiElement = event.getData(CommonDataKeys.PSI_ELEMENT);
        // 判断是否为方法
        // 从 PsiElement 向上遍历找到所属的 PsiJavaFile 文件对象
        PsiJavaFile javaFile = PsiTreeUtil.getParentOfType(psiElement, PsiJavaFile.class);
        // 判断该文件是否位于 src/main/java 目录下
        if (javaFile != null && isInSrcMainJava(javaFile)) {
            if (psiElement instanceof PsiMethod) {
                PsiMethod method = (PsiMethod) psiElement;
                // 启用按钮并设置可见
                event.getPresentation().setEnabledAndVisible(true);
            }
        }
    }

    private boolean isInSrcMainJava(PsiJavaFile javaFile) {
        String filePath = javaFile.getVirtualFile().getPath();
        return filePath.contains("/src/main/java/");
    }

    private boolean isMouseOnMethod(AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return false;
        }
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (method != null) {
            return true;
        }
        return false;
    }

    public void init(Project project, String basePath,MavenProject mavenProject) {
        config = new Config.ConfigBuilder()
                .project(project)
                .mavenProject(mavenProject)
                .basePath(basePath)
                .apiKeys(WindowConfig.apiKeys)
                .enableMultithreading(WindowConfig.enableMultithreading)
                .tmpOutput(Paths.get(basePath, WindowConfig.tmpOutput))
                .testOutput(Paths.get(basePath, WindowConfig.testOutput))
                .stopWhenSuccess(WindowConfig.stopWhenSuccess)
                .noExecution(WindowConfig.noExecution)
                .maxThreads(WindowConfig.maxThreads)
                .testNumber(WindowConfig.testNumber)
                .maxRounds(WindowConfig.maxRounds)
                .minErrorTokens(WindowConfig.minErrorTokens)
                .maxPromptTokens(WindowConfig.maxPromptTokens)
                .model(WindowDefaultConfig.models[WindowConfig.model_index])
                .temperature(WindowConfig.temperature)
                .topP(WindowConfig.topP)
                .frequencyPenalty(WindowConfig.frequencyPenalty)
                .presencePenalty(WindowConfig.presencePenalty)
                .proxy((WindowConfig.hostname.equals("") || WindowConfig.port.equals("")) ? "null:-1" : String.format("%s:%s", WindowConfig.hostname, WindowConfig.port))
                .others()
                .build();
    }
}
