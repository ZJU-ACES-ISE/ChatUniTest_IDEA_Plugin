package zju.cst.aces.chatunitest_plugin_idea;

import zju.cst.aces.Windows.WindowConfig;
import zju.cst.aces.Windows.WindowDefaultConfig;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;
import zju.cst.aces.config.Config;

import java.nio.file.Paths;

public class TestPluginAction extends AnAction{
    Config config;

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        MavenProject mavenProject = MavenActionUtil.getMavenProject(event.getDataContext());
        String basePath= project.getBasePath();
        init(project,basePath,mavenProject);
        String folderPath= String.valueOf(config.testOutput);
        VirtualFile folder = LocalFileSystem.getInstance().refreshAndFindFileByPath(folderPath);
        if(folder!=null&&folder.isDirectory()){
            folder.refresh(false,true);
        }
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
