package zju.cst.aces.actions;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import zju.cst.aces.IDEA.config.WindowConfig;
import zju.cst.aces.IDEA.config.WindowDefaultConfig;
import zju.cst.aces.IDEA.logger.IdeaLogger;
import zju.cst.aces.IDEA.runner.ValidatorImpl;
import zju.cst.aces.ProjectImpl;
import zju.cst.aces.api.Logger;
import zju.cst.aces.api.config.Config;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestGeneration {
    public Config config;

    public void init(Project project, Module module, Application application){
        Logger log=new IdeaLogger(project,application);
        String rootPath = project.getBasePath();
        String basePath = Paths.get(module.getModuleFilePath()).getParent().toString();
        File testOutput=null;//默认生成在模块下
        File tmpOutput = Paths.get(rootPath, "/tmp/chatunitest-info").toFile();//默认在项目根目录下
        MavenProjectsManager mavenProjectsManager = MavenProjectsManager.getInstance(project);
        List<MavenProject> mavenProjects = mavenProjectsManager.getProjects();
        MavenProject mavenProject=null;
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
        config = new Config.ConfigBuilder(new ProjectImpl(project,module))
                .log(log)
                .classPaths(listClassPaths(mavenProject))
                .promptPath(null)
                .examplePath(Paths.get(basePath,"exampleUsage.json"))
                .apiKeys(WindowConfig.apiKeys)
                .enableMultithreading(WindowConfig.enableMultithreading)
                .enableRuleRepair(true)
                .tmpOutput(tmpOutput.toPath())
                .testOutput(testOutput == null? null : testOutput.toPath())
                .stopWhenSuccess(WindowConfig.stopWhenSuccess)
                .noExecution(WindowConfig.noExecution)
                .enableObfuscate(false)
                .enableMerge(true)
                .obfuscateGroupIds(new String[]{})
                .maxThreads(WindowConfig.maxThreads)
                .testNumber(WindowConfig.testNumber)
                .maxRounds(WindowConfig.maxRounds)
                .maxPromptTokens(WindowConfig.maxPromptTokens)
                .minErrorTokens(WindowConfig.minErrorTokens)
                .sleepTime(0)
                .dependencyDepth(1)
                .model(WindowDefaultConfig.models[WindowConfig.model_index])
                .url("https://api.openai.com/v1/chat/completions")
                .temperature(WindowConfig.temperature)
                .topP(WindowConfig.topP)
                .frequencyPenalty(WindowConfig.frequencyPenalty)
                .presencePenalty(WindowConfig.presencePenalty)
                .proxy((WindowConfig.hostname.equals("") || WindowConfig.port.equals("")) ? "null:-1" : String.format("%s:%s", WindowConfig.hostname, WindowConfig.port))
                .build();
        config.setValidator(new ValidatorImpl(config.testOutput,config.compileOutputPath,config.project.getBasedir().toPath().resolve("target"),config.classPaths,config.project));
        System.out.println("config = " + config.toString());
    }
    public static List<String> listClassPaths(MavenProject mavenProject) {
        ArrayList<String> classPaths = new ArrayList<>();
        for (MavenArtifact dependency : mavenProject.getDependencies()) {
            classPaths.add(dependency.getPath());
        }
        classPaths.add(mavenProject.getOutputDirectory());//{module}/target/class
        classPaths.add(mavenProject.getTestOutputDirectory());//{module}/target/testclass
        String buildDirectory = mavenProject.getBuildDirectory();
        String finalName = mavenProject.getFinalName();
        // 构建 Artifact 路径
        classPaths.add(String.valueOf(Paths.get(buildDirectory).resolve(finalName + ".jar")));
        return classPaths;
    }
}
