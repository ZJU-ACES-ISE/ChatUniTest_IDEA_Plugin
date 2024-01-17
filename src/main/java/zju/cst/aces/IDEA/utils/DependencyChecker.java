package zju.cst.aces.IDEA.utils;

import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.project.MavenProject;

public class DependencyChecker {
    //根据字符串匹配，判断是否已经添加依赖
    public static boolean hasDependencyWithGroupId(MavenProject mavenProject, String groupId,String artifactId) {
        for (MavenArtifact dependency : mavenProject.getDependencies()) {
            if(dependency.getGroupId().toString().equals(groupId)&&dependency.getArtifactId().toString().equals(artifactId)){
                return true;
            }
        }
        return false;
    }

    //方法暂时无效
    public static void addDependencyToProject(MavenProject mavenProject,String groupId,String artifactId,String version){
        MavenArtifact newDependency = new MavenArtifact(
                groupId,
                artifactId,
                version,
                version, // 基础版本通常等于版本
                "pom", // 你的依赖类型
                null, // 如果没有分类器，可以设置为 null
                "compile", // 依赖范围
                false, // 是否可选
                "pom", // 依赖的扩展名
                null, // 文件，可以设置为 null
                mavenProject.getLocalRepository(),
                true, // 是否已解析
                false // 是否是存根
        );
        System.out.println("mavenProject = " + mavenProject.getLocalRepository().toPath().toString());
        mavenProject.addDependency(newDependency);
    }
}
