package zju.cst.aces.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.maven.plugin.logging.Log;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;
import zju.cst.aces.IDEA.utils.JudgeUtil;
import zju.cst.aces.IDEA.utils.UpdateGitignoreUtil;
import zju.cst.aces.api.impl.Parser;
import zju.cst.aces.parser.ProjectParser;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import zju.cst.aces.api.config.Config;


public class ChatUniTestPluginAction extends AnAction {
    public Config config;
    public static Log log;

    @Override
    public void actionPerformed(AnActionEvent event) {
        Application application = ApplicationManager.getApplication();
        application.executeOnPooledThread(() -> {
            Project project = event.getProject();
            String basePath = project.getBasePath();
            MavenProject mavenProject = MavenActionUtil.getMavenProject(event.getDataContext());
            //找到当前的module
            //todo:查看是否是gradle或者maven项目
            //查看是否是maven或gradle项目

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

            VirtualFile virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);
            CompilerManager compilerManager = CompilerManager.getInstance(project);
            VirtualFile currentFile = event.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);

            //parse the project
            CompletableFuture<Integer> parseFuture = new CompletableFuture<>();
            application.invokeLater(() -> {
                int result = Messages.showDialog(
                        project,
                        "After the project is updated, it needs to be reparsed ",
                        "Parse Warning",
                        new String[]{"Parse the project", "Skip"},
                        0,
                        Messages.getWarningIcon()
                );
                parseFuture.complete(result);
            });
            try {
                int result=parseFuture.get();
                if(result==0){
                    CompletableFuture<Void> classRunnerTask = CompletableFuture.runAsync(() -> {
                        application.invokeLater(() -> {
                            log.info("[ChatUniTest] Parsing class info");
                        });
                        new Parser(config).process();
                    });
                    try {
                        classRunnerTask.get();
                        application.invokeLater(() -> {
                            log.info("[ChatUniTest] Project parse finished");
                        });
                    } catch (InterruptedException | ExecutionException e) {
                        application.invokeLater(() -> {
                            log.info( "[ChatUniTest] Project parse failed");
                            e.printStackTrace();
                        });
                        throw new RuntimeException(e);
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            //判断是否能够调用插件
            if (basePath.equals(virtualFile.getPath())) {
                ProjectTestGeneration.generate_project_test(config);
            } else if (virtualFile.getFileType() == FileTypeManager.getInstance().getFileTypeByExtension("java") && !isMouseOnMethod(event)) {
                HashMap<String,String> resultMap=application.runReadAction((Computable<HashMap<String, String>>)()->{
                    HashMap<String, String> map = new HashMap<>();
                    PsiJavaFile psiJavaFile = (PsiJavaFile) event.getData(CommonDataKeys.PSI_FILE);
                    String fullClassName = String.format("%s.%s", psiJavaFile.getPackageName(), virtualFile.getNameWithoutExtension());
                    String simpleClassName = virtualFile.getNameWithoutExtension();
                    map.put("fullClassName",fullClassName);
                    map.put("simpleClassName",simpleClassName);
                    return map;
                });
                ClassTestGeneration.generate_class_test(config, resultMap.get("fullClassName"), resultMap.get("simpleClassName"));
            } else if (isMouseOnMethod(event)) {
                String[] fullClassName = application.runReadAction((Computable<String[]>) () -> {
                    PsiJavaFile psiJavaFile = (PsiJavaFile) event.getData(CommonDataKeys.PSI_FILE);
                    String fcName = String.format("%s.%s", psiJavaFile.getPackageName(), virtualFile.getNameWithoutExtension());
                    String simpleClassName = virtualFile.getNameWithoutExtension();
                    return new String[]{fcName, simpleClassName};
                });
                //获取方法体

                PsiFileInfo psiFileInfo=application.runReadAction((Computable<PsiFileInfo>)()->{
                    Editor editor = event.getData(CommonDataKeys.EDITOR);
                    PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
                    int offset = editor.getCaretModel().getOffset();
                    PsiElement element = psiFile.findElementAt(offset);
                    PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
                    String methodName = JudgeUtil.getMethodName(event);
                    String methodBody=containingMethod.getText();
                    PsiImportList importList = PsiTreeUtil.getChildOfType(psiFile, PsiImportList.class);
                    PsiFileInfo info = new PsiFileInfo(methodName,methodBody,importList);
                    return info;
                } );

                MethodTestGeneration.generate_method_test(config, fullClassName[0], fullClassName[1],
                        psiFileInfo.getMethodName(),psiFileInfo.getMethodBody(), psiFileInfo.getImportList());
            }
        });
    }
    public class PsiFileInfo {
        private String methodName;
        private String methodBody;
        private PsiImportList importList;

        public PsiFileInfo(String methodName, String methodBody, PsiImportList importList) {
            this.methodName = methodName;
            this.methodBody = methodBody;
            this.importList = importList;
        }

        public PsiFileInfo() {
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getMethodBody() {
            return methodBody;
        }

        public void setMethodBody(String methodBody) {
            this.methodBody = methodBody;
        }

        public PsiImportList getImportList() {
            return importList;
        }

        public void setImportList(PsiImportList importList) {
            this.importList = importList;
        }
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

}
