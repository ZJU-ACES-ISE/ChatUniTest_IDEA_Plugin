package zju.cst.aces.IDEA.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import zju.cst.aces.api.config.Config;

import java.io.File;

public class DefaultValue {
    public File testOutput;
    public File tmpOutput;
    public File promptPath;
    public File examplePath;
    public String url= "https://api.openai.com/v1/chat/completions";
    public String model= "gpt-3.5-turbo";
    public String[] apiKeys;
    public boolean stopWhenSuccess= true;
    public boolean noExecution=false;
    public boolean enableMultithreading=true;
    public boolean enableRuleRepair=true;
    public boolean enableObfuscate=false;
    public boolean enableMerge=true;
    public String[] obfuscateGroupIds;
    public int maxThreads=0;
    public int testNumber=5;
    public int maxRounds=5;
    public int maxPromptTokens=2600;
    public int minErrorTokens=500;
    public int sleepTime=0;
    public int dependencyDepth=1;
    public Double temperature=0.5;
    public  int topP=1;
    public int frequencyPenalty=0;
    public int presencePenalty=0;
    public String proxy= "null:-1";
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public DependencyGraphBuilder dependencyGraphBuilder;
    public static Log log;
    public Config config;

}
