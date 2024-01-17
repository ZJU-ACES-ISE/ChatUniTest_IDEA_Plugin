package zju.cst.aces.IDEA.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JudgeUtil {
    public static boolean isMavenProject(Project project) {
        MavenProjectsManager mavenProjectsManager = MavenProjectsManager.getInstance(project);
        if (!mavenProjectsManager.hasProjects()) {
            return true;
        }
        return false;
    }

    public static boolean isMavenOrGradle(Project project){
        PsiManager psiManager = PsiManager.getInstance(project);
        VirtualFile pomFile = project.getBaseDir().findChild("pom.xml");
        VirtualFile gradleFile = project.getBaseDir().findChild("build.gradle");
        if(pomFile!=null||gradleFile!=null){
            return true;
        }
        return false;
    }

    public static boolean isJavaFile(AnActionEvent event) {
        VirtualFile virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        if ((virtualFile.getFileType() == FileTypeManager.getInstance().getFileTypeByExtension("java"))) {
            return true;
        }
        return false;
    }

    public static boolean inRightDirectory(Project project, VirtualFile virtualFile) {
        System.out.println("project = " + project.getBasePath());
        System.out.println("virtualFile = " + virtualFile.getPath());
        Path pathBase = Paths.get(project.getBasePath(), "src", "main", "java");
        Path filePath = Paths.get(virtualFile.getPath());
        if (filePath.startsWith(pathBase)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isRootProject(Project project, VirtualFile virtualFile) {
        if (!(virtualFile.isDirectory() && project.getName().equals(virtualFile.getName()))) {
            return false;
        }
        return true;
    }

    public boolean hasConfigProperties(Project project) {
        VirtualFile baseDir = project.getBaseDir();
        VirtualFile resourcesFile = baseDir.findChild("src").findChild("main").findChild("resources").findChild("plugin_config.properties");
        if (resourcesFile == null) {
            return false;
        }
        return true;
    }

    public static String getMethodName(AnActionEvent event){
        return ApplicationManager.getApplication().runReadAction((Computable<String>) ()->{
            Editor editor = event.getData(CommonDataKeys.EDITOR);
            PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
            int offset = editor.getCaretModel().getOffset();
            PsiElement element = psiFile.findElementAt(offset);
            PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
            String methodName = method.getName();
            return methodName;
        });
    }
}
