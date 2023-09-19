package zju.cst.aces.actions;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileStatusNotification;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.Computable;
import org.jetbrains.annotations.NotNull;
import zju.cst.aces.Windows.Panels.ProjectSettingPanel;
import zju.cst.aces.Windows.Panels.SettingPanel;
import zju.cst.aces.Windows.WindowConfig;
import zju.cst.aces.Windows.WindowDefaultConfig;
import zju.cst.aces.config.ConfigPersistence;
import zju.cst.aces.config.ProjectConfigPersistence;
import zju.cst.aces.utils.*;
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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ChatUniTestPluginAction extends AnAction {
    Config config;
    public Integer configMode;

    @Override
    public void actionPerformed(AnActionEvent event) {
        Application application = ApplicationManager.getApplication();
        config.application = application;
        application.executeOnPooledThread(() -> {
            Project project = event.getProject();
            String basePath = project.getBasePath();
            MavenProject mavenProject = MavenActionUtil.getMavenProject(event.getDataContext());
            if (JudgeUtil.isMavenProject(project)) {
                application.invokeLater(() -> {
                    Messages.showMessageDialog("Please use maven-archetype project", "Error", Messages.getErrorIcon());
                });
                return;
            }
            CompletableFuture<Integer> dependencyFuture = new CompletableFuture<>();
            if (!DependencyChecker.hasDependencyWithGroupId(mavenProject, "io.github.ZJU-ACES-ISE", "chatunitest-starter")) {
                application.invokeLater(() -> {
                    int result = Messages.showDialog(
                            project,
                            "Seems like “chatunitest-starter” dependency is missing",
                            "Warning",
                            new String[]{"Ignore", "Stop"},
                            0,
                            Messages.getWarningIcon()
                    );
                    dependencyFuture.complete(result);
                });
                try {
                    int addDependencyChoice = dependencyFuture.get();
                    if (addDependencyChoice == 1) {
                        return;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            //询问用户需要使用全局的配置还是项目级别的配置
            CompletableFuture<Integer> configFuture = new CompletableFuture<>();
            application.invokeLater(() -> {
                int result = Messages.showDialog(
                        project,
                        "Please choose a config mode (Global Configuration or Project-Level Configuration)",
                        "Config Setting",
                        new String[]{"Global", "Project"},
                        0,
                        Messages.getWarningIcon()
                );
                configFuture.complete(result);
            });
            try {
                configMode = configFuture.get();
                if (configMode == 1) {
                    Path persistenceXmlPath = Paths.get(project.getBasePath(), ".idea", project.getName() + ".xml");
                    if(!Files.exists(persistenceXmlPath)){
                        ProjectSettingPanel projectSettingPanel = new ProjectSettingPanel(event);
                        projectSettingPanel.show();
                        return;
                    }
                    //加载该项目级别的配置
                    loadPersistentConfig(1,persistenceXmlPath);
                    if (WindowConfig.apiKeys == null || WindowConfig.apiKeys[0].equals("")) {
                        application.invokeLater(() -> {
                            ProjectSettingPanel projectSettingPanel = new ProjectSettingPanel(event);
                            projectSettingPanel.show();
                        });
                        return;
                    }
                }
                else{
                    //加载全局配置
                    loadPersistentConfig(0,null);
                    if (WindowConfig.apiKeys == null || WindowConfig.apiKeys[0].equals("")) {
                        application.invokeLater(() -> {
                            SettingPanel settingPanel = new SettingPanel();
                            settingPanel.show();
                        });
                        return;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }

            //设置gitignore，防止中间文件产生过程中经常提示用户添加到git中（用户也可以在idea中自行设置）
            application.invokeLater(() -> {
                application.runWriteAction(() -> {
                    Path path = Paths.get(project.getBasePath(), ".gitignore");
                    File file = path.toFile();
                    //没有gitignore则无需处理
                    if (file.exists()) {
                        UpdateGitignoreUtil.addToFile(file);
                    }
                });
            });

            init(project, basePath, mavenProject);
            if (!ConnectUtil.testOpenApiConnection(WindowConfig.apiKeys, WindowConfig.hostname, WindowConfig.port)) {
                application.invokeLater(() -> {
                    Messages.showMessageDialog("Connect to OpenAI failed", "Error", Messages.getErrorIcon());
                });
                return;
            }
            VirtualFile virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);


            CompilerManager compilerManager = CompilerManager.getInstance(project);
            VirtualFile currentFile = event.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);

            ConfigPersistence configPersistence = application.getComponent(ConfigPersistence.class);
            ConfigPersistence.IdeaConfiguration ideaConfiguration = configPersistence.getState();

            if (WindowConfig.compileReminder == true) {
                Module module = ModuleUtilCore.findModuleForFile(currentFile, project);
                CompletableFuture<Integer> compileDialogResult = new CompletableFuture<>();
                application.invokeLater(() -> {
                    int result = Messages.showDialog(
                            project,
                            "Insure the project has been compiled and parsed",
                            "Compile Confirm",
                            new String[]{"Skip", "Compile"},
                            0, // Default option (0: "Skip", 1: "Compile")
                            Messages.getWarningIcon()
                    );
                    compileDialogResult.complete(result);
                });
                try {
                    if (compileDialogResult.get() == 1) {
                        CompletableFuture<Boolean> compileFuture = new CompletableFuture<>();
                        application.invokeLater(() -> {
                            compilerManager.compile(module, new CompileStatusNotification() {
                                @Override
                                public void finished(boolean aborted, int errors, int warnings, @NotNull CompileContext compileContext) {
                                    if (errors > 0) {
                                        application.invokeLater(() -> {
                                            Messages.showMessageDialog("Compile project failed", "Error", Messages.getErrorIcon());
                                        });
                                        compileFuture.complete(false);
                                    } else {
                                        compileFuture.complete(true);
                                    }
                                }
                            });
                        });
                        try {
                            if (!compileFuture.get()) {
                                return;
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            //parse the project
            ProjectParser parser = new ProjectParser(config);
            CompletableFuture<Void> classRunnerTask = CompletableFuture.runAsync(() -> {
                application.invokeLater(() -> {
                    LoggerUtil.info(project, "[ChatUniTest] Parsing class info");
                });
                parser.parse();
            });
            try {
                classRunnerTask.get();
                application.invokeLater(() -> {
                    LoggerUtil.info(project, "[ChatUniTest] Project parse finished");
                });
            } catch (InterruptedException | ExecutionException e) {
                application.invokeLater(() -> {
                    LoggerUtil.info(project, "[ChatUniTest] Project parse failed");
                });
                throw new RuntimeException(e);
            }
            if (basePath.equals(virtualFile.getPath())) {
                ProjectTestGeneration.generate_project_test(config);
            } else if (virtualFile.getFileType() == FileTypeManager.getInstance().getFileTypeByExtension("java") && !isMouseOnMethod(event)) {
                PsiJavaFile psiJavaFile = (PsiJavaFile) event.getData(CommonDataKeys.PSI_FILE);
                String fullClassName = String.format("%s.%s", psiJavaFile.getPackageName(), virtualFile.getNameWithoutExtension());
                String simpleClassName = virtualFile.getNameWithoutExtension();
                ClassTestGeneration.generate_class_test(config, fullClassName, simpleClassName);
            } else if (isMouseOnMethod(event)) {
                String[] fullClassName = application.runReadAction((Computable<String[]>) () -> {
                    PsiJavaFile psiJavaFile = (PsiJavaFile) event.getData(CommonDataKeys.PSI_FILE);
                    String fcName = String.format("%s.%s", psiJavaFile.getPackageName(), virtualFile.getNameWithoutExtension());
                    String simpleClassName = virtualFile.getNameWithoutExtension();
                    return new String[]{fcName, simpleClassName};
                });
                String methodName = JudgeUtil.getMethodName(event);
                MethodTestGeneration.generate_method_test(config, fullClassName[0], fullClassName[1], methodName);
            }
        });
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
        return ApplicationManager.getApplication().runReadAction((Computable<Boolean>) () -> {
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
        });
    }

    public void init(Project project, String basePath, MavenProject mavenProject) {
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

    public void loadPersistentConfig(int mode,Path persistenceXmlPath) {
        //项目级别
        if(mode==1){

            ProjectConfigPersistence projectConfigPersistence = ProjectConfigFileUtil.loadProjectConfig(persistenceXmlPath.toFile().getPath());
            WindowConfig.apiKeys=projectConfigPersistence.apiKeys;
            WindowConfig.enableMultithreading=projectConfigPersistence.enableMultithreading;
            WindowConfig.tmpOutput=projectConfigPersistence.tmpOutput;
            WindowConfig.testOutput=projectConfigPersistence.testOutput;
            WindowConfig.stopWhenSuccess=projectConfigPersistence.stopWhenSuccess;
            WindowConfig.noExecution=projectConfigPersistence.noExecution;
            WindowConfig.maxThreads=projectConfigPersistence.maxThreads;
            WindowConfig.minErrorTokens=projectConfigPersistence.minErrorTokens;
            WindowConfig.maxPromptTokens=projectConfigPersistence.maxPromptTokens;
            WindowConfig.model_index=projectConfigPersistence.model_index;
            WindowConfig.temperature=projectConfigPersistence.temperature;
            WindowConfig.topP=projectConfigPersistence.topP;
            WindowConfig.frequencyPenalty=projectConfigPersistence.frequencyPenalty;
            WindowConfig.presencePenalty=projectConfigPersistence.presencePenalty;
            WindowConfig.hostname=projectConfigPersistence.hostname != null ? projectConfigPersistence.hostname : "";
            WindowConfig.port=projectConfigPersistence.port != null ? projectConfigPersistence.port : "";
            WindowConfig.compileReminder=projectConfigPersistence.remind_compile;
            WindowConfig.regenerateReminder=projectConfigPersistence.remind_regenerate;
            WindowConfig.repairReminder=projectConfigPersistence.remind_repair;
            WindowConfig.notifyRepair=projectConfigPersistence.notifyRepair;
            System.out.println("project mode config");
        }
        //全局
        else {
            ConfigPersistence configPersistence = ApplicationManager.getApplication().getComponent(ConfigPersistence.class);
            ConfigPersistence.IdeaConfiguration ideaConfiguration = configPersistence.getState();
            String[] apiKeys_per = ideaConfiguration.apiKeys;
            String hostname_per = ideaConfiguration.hostname;
            String port_per = ideaConfiguration.port;
            Integer testNumber_per = ideaConfiguration.testNumber;
            Integer maxRounds_per = ideaConfiguration.maxRounds;
            Integer minErrorTokens_per = ideaConfiguration.minErrorTokens;
            Integer model_index_per = ideaConfiguration.model_index;
            Integer topP_per = ideaConfiguration.topP;
            Double temperature_per = ideaConfiguration.temperature;
            Integer frequencyPenalty_per = ideaConfiguration.frequencyPenalty;
            Integer presencePenalty_per = ideaConfiguration.presencePenalty;
            String tmpOutput_per = ideaConfiguration.tmpOutput;
            Boolean stopWhenSuccess_per = ideaConfiguration.stopWhenSuccess;
            Boolean enableMultithreading_per = ideaConfiguration.enableMultithreading;
            Boolean noExecution_per = ideaConfiguration.noExecution;
            Integer maxThreads_per = ideaConfiguration.maxThreads;
            String testOutput_per = ideaConfiguration.testOutput;
            Integer maxPromptTokens_per = ideaConfiguration.maxPromptTokens;
            Boolean repairReminder = ideaConfiguration.remind_repair;
            Boolean regenerateReminder = ideaConfiguration.remind_regenerate;
            Boolean compileReminder = ideaConfiguration.remind_compile;
            Integer notifyRepair_per = ideaConfiguration.notifyRepair;
            WindowConfig.apiKeys = apiKeys_per;
            WindowConfig.hostname = hostname_per != null ? hostname_per : "";
            WindowConfig.port = port_per != null ? port_per : "";
            WindowConfig.testNumber = testNumber_per;
            WindowConfig.maxRounds = maxRounds_per;
            WindowConfig.minErrorTokens = minErrorTokens_per;
            WindowConfig.topP = topP_per;
            WindowConfig.temperature = temperature_per;
            WindowConfig.frequencyPenalty = frequencyPenalty_per;
            WindowConfig.presencePenalty = presencePenalty_per;
            WindowConfig.tmpOutput = tmpOutput_per;
            WindowConfig.testOutput = testOutput_per;
            WindowConfig.maxPromptTokens = maxPromptTokens_per;
            WindowConfig.model_index = model_index_per;
            WindowConfig.maxThreads = maxThreads_per;
            WindowConfig.stopWhenSuccess = stopWhenSuccess_per;
            WindowConfig.enableMultithreading = enableMultithreading_per;
            WindowConfig.noExecution = noExecution_per;
            WindowConfig.compileReminder = compileReminder;
            WindowConfig.repairReminder = repairReminder;
            WindowConfig.regenerateReminder = regenerateReminder;
            WindowConfig.notifyRepair = notifyRepair_per;
            System.out.println("global config setting");
        }
    }
}
