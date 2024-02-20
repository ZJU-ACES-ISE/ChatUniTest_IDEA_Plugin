package zju.cst.aces;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import zju.cst.aces.api.Project;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProjectImpl implements Project {
    public MavenProject mavenProject;
    public Module module;
    public com.intellij.openapi.project.Project project;

    public ProjectImpl(com.intellij.openapi.project.Project project, Module module) {
        this.project = project;
        this.module=module;
        MavenProjectsManager mavenProjectsManager = MavenProjectsManager.getInstance(project);
        // 获取当前模块对应的 Maven 项目
        List<MavenProject> mavenProjects = mavenProjectsManager.getProjects();
        String moduleFilePath = module.getModuleFilePath();
        //获取当前module对应的project，并赋值
        for (MavenProject mp : mavenProjects) {
            String modulePath = Paths.get(module.getModuleFilePath()).getParent().toString().replace("\\", File.separator).replace("/", File.separator);
            //判断是否是父模块
            if(modulePath.contains(".idea")){
                modulePath=Paths.get(module.getModuleFilePath()).getParent().getParent().toString().replace("\\", File.separator).replace("/", File.separator);
            }
            String mpPath = mp.getDirectory().toString().replace("\\", File.separator).replace("/", File.separator);
            //通过遍历所有模块来获取当前模块
            if (mpPath.equals(modulePath)) {
                mavenProject=mp;
                break;
            }
        }
    }

    @Override
    public Project getParent() {
        //如果路径中包含".idea"，则认定为父项目,parent为null
        if(Paths.get(module.getModuleFilePath()).getParent().toString().contains(".idea")){
            return null;
        }
        Module[] modules = ModuleManager.getInstance(project).getModules();
        //遍历所有module，找到父module
        for (Module md : modules) {
            if(Paths.get(md.getModuleFilePath()).getParent().toString().contains(".idea")){
                return new ProjectImpl(project,md);
            }
        }
        return null;
    }

    @Override
    public File getBasedir() {
        File basedir = new File(mavenProject.getDirectory());
        return basedir;
    }

    @Override
    public String getPackaging() {
        return mavenProject.getPackaging();
    }

    @Override
    public String getGroupId() {
        return mavenProject.getMavenId().getGroupId();
    }

    @Override
    public String getArtifactId() {
        return mavenProject.getMavenId().getArtifactId();
    }

    @Override
    public List<String> getCompileSourceRoots() {
        ArrayList<String> compileSourceRoots = new ArrayList<>();
        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        return mavenProject.getSources();
        /*// 获取编译源根路径
        VirtualFile[] sourceRoots = moduleRootManager.getSourceRoots();
        for (VirtualFile sourceRoot : sourceRoots) {
            compileSourceRoots.add(sourceRoot.getPath());
        }
        return compileSourceRoots;*/
    }

    @Override
    public Path getArtifactPath() {
        String buildDirectory = mavenProject.getBuildDirectory();
        String finalName = mavenProject.getFinalName();
        // 构建 Artifact 路径
        return Paths.get(buildDirectory).resolve(finalName + ".jar");
    }

    @Override
    public Path getBuildPath() {
        CompilerModuleExtension extension = CompilerModuleExtension.getInstance(module);
        // 获取输出目录
        VirtualFile outputDirectory = extension.getCompilerOutputPath();
        if (outputDirectory != null) {
            return Path.of(outputDirectory.getPath());
        }
        return null;
    }
}
