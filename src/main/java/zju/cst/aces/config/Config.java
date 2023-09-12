package zju.cst.aces.config;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.jetbrains.idea.maven.project.MavenProject;
import zju.cst.aces.util.TestCompiler;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Config {
//    public MavenSession session;
//    public MavenProject project;
    public DependencyGraphBuilder dependencyGraphBuilder;
    public List<String> classPaths;
    public String[] apiKeys;
    public Log log;
    public String OS;
    public boolean stopWhenSuccess;
    public boolean noExecution;
    public boolean enableMultithreading;
    public int maxThreads;
    public int classThreads;
    public int methodThreads;
    public int testNumber;
    public int maxRounds;
    public int maxPromptTokens;
    public int minErrorTokens;
    public String model;
    public Double temperature;
    public int topP;
    public int frequencyPenalty;
    public int presencePenalty;
    public Path testOutput;
    public Path tmpOutput;
    public Path parseOutput;
    public Path errorOutput;
    public Path classMapPath;

    public String proxy;
    public String hostname;
    public String port;
    public OkHttpClient client;
    public String basePath;
    public Project project;
    public MavenProject mavenProject;
    public static Application application;
//    public static boolean regenerate_record;
    public Integer repair_record=0;
    public Integer regenerate_record=0;


    public Integer getRegenerate_record() {
        return regenerate_record;
    }

    public void setRegenerate_record(Integer regenerate_record) {
        this.regenerate_record = regenerate_record;
    }

    public Integer getRepair_record() {
        return repair_record;
    }

    public void setRepair_record(Integer repair_record) {
        this.repair_record = repair_record;
    }

    public static class ConfigBuilder {
        public DependencyGraphBuilder dependencyGraphBuilder;
        public List<String> classPaths;
        public String[] apiKeys;
        public Log log;
        public String OS = System.getProperty("os.name").toLowerCase();
        public boolean stopWhenSuccess = true;
        public boolean noExecution = false;
        public boolean enableMultithreading = true;
        public int maxThreads = Runtime.getRuntime().availableProcessors() * 5;
        public int classThreads = (int) Math.ceil((double)  this.maxThreads / 10);
        public int methodThreads = (int) Math.ceil((double) this.maxThreads / this.classThreads);
        public int testNumber = 5;
        public int maxRounds = 5;
        public int maxPromptTokens = 2600;
        public int minErrorTokens = 500;
        public String model = "gpt-3.5-turbo";
        public Double temperature = 0.5;
        public int topP = 1;
        public int frequencyPenalty = 0;
        public int presencePenalty = 0;
        public Path testOutput;
        public Path tmpOutput;
        public Path parseOutput;
        public Path errorOutput;
        public Path classMapPath;
        public String basePath;
        public Project project;
        public MavenProject mavenProject;

        public String proxy = "null:-1";
        public String hostname = "null";
        public String port = "-1";
        public OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();

        public ConfigBuilder others() {
            this.classPaths = TestCompiler.listClassPaths(this.mavenProject);
            this.dependencyGraphBuilder = dependencyGraphBuilder;
            this.log = log;
            this.parseOutput = this.tmpOutput.resolve("class-info");
            this.errorOutput = this.tmpOutput.resolve("error-message");
            this.classMapPath = this.tmpOutput.resolve("class-map.json");
            return this;
        }

        public ConfigBuilder(){

        }

        public ConfigBuilder maxThreads(int maxThreads) {
            if (maxThreads <= 0) {
                this.maxThreads = Runtime.getRuntime().availableProcessors() * 5;
            } else {
                this.maxThreads = maxThreads;
            }
            this.classThreads = (int) Math.ceil((double)  this.maxThreads / 10);
            this.methodThreads = (int) Math.ceil((double) this.maxThreads / this.classThreads);
            if (this.stopWhenSuccess == false) {
                this.methodThreads = (int) Math.ceil((double)  this.methodThreads / this.testNumber);
            }
            return this;
        }

        public ConfigBuilder proxy(String proxy) {
            setProxy(proxy);
            return this;
        }

        public ConfigBuilder tmpOutput(Path tmpOutput) {
            this.tmpOutput = tmpOutput;
            this.parseOutput = this.tmpOutput.resolve("class-info");
            this.errorOutput = this.tmpOutput.resolve("error-message");
            this.classMapPath = this.tmpOutput.resolve("class-map.json");
            return this;
        }





        public ConfigBuilder dependencyGraphBuilder(DependencyGraphBuilder dependencyGraphBuilder) {
            this.dependencyGraphBuilder = dependencyGraphBuilder;
            return this;
        }

        public ConfigBuilder classPaths(List<String> classPaths) {
            this.classPaths = classPaths;
            return this;
        }

        public ConfigBuilder log(Log log) {
            this.log = log;
            return this;
        }

        public ConfigBuilder OS(String OS) {
            this.OS = OS;
            return this;
        }

        public ConfigBuilder stopWhenSuccess(boolean stopWhenSuccess) {
            this.stopWhenSuccess = stopWhenSuccess;
            return this;
        }

        public ConfigBuilder noExecution(boolean noExecution) {
            this.noExecution = noExecution;
            return this;
        }

        public ConfigBuilder enableMultithreading(boolean enableMultithreading) {
            this.enableMultithreading = enableMultithreading;
            return this;
        }

        public ConfigBuilder classThreads(int classThreads) {
            this.classThreads = classThreads;
            return this;
        }

        public ConfigBuilder methodThreads(int methodThreads) {
            this.methodThreads = methodThreads;
            return this;
        }

        public ConfigBuilder apiKeys(String[] apiKeys) {
            this.apiKeys = apiKeys;
            return this;
        }

        public ConfigBuilder mavenProject(MavenProject mavenProject){
            this.mavenProject=mavenProject;
            return this;
        }

        public ConfigBuilder basePath(String basePath){
            this.basePath=basePath;
            return this;
        }

        public ConfigBuilder project(Project project){
            this.project=project;
            return this;
        }

        public ConfigBuilder testNumber(int testNumber) {
            this.testNumber = testNumber;
            return this;
        }

        public ConfigBuilder maxRounds(int maxRounds) {
            this.maxRounds = maxRounds;
            return this;
        }

        public ConfigBuilder maxPromptTokens(int maxPromptTokens) {
            this.maxPromptTokens = maxPromptTokens;
            return this;
        }

        public ConfigBuilder minErrorTokens(int minErrorTokens) {
            this.minErrorTokens = minErrorTokens;
            return this;
        }

        public ConfigBuilder model(String model) {
            this.model = model;
            return this;
        }

        public ConfigBuilder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public ConfigBuilder topP(int topP) {
            this.topP = topP;
            return this;
        }

        public ConfigBuilder frequencyPenalty(int frequencyPenalty) {
            this.frequencyPenalty = frequencyPenalty;
            return this;
        }

        public ConfigBuilder presencePenalty(int presencePenalty) {
            this.presencePenalty = presencePenalty;
            return this;
        }

        public ConfigBuilder testOutput(Path testOutput) {
            this.testOutput = testOutput;
            return this;
        }

        public ConfigBuilder parseOutput(Path parseOutput) {
            this.parseOutput = parseOutput;
            return this;
        }

        public ConfigBuilder errorOutput(Path errorOutput) {
            this.errorOutput = errorOutput;
            return this;
        }

        public ConfigBuilder classMapPath(Path classMapPath) {
            this.classMapPath = classMapPath;
            return this;
        }

        public ConfigBuilder hostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public ConfigBuilder port(String port) {
            this.port = port;
            return this;
        }

        public ConfigBuilder client(OkHttpClient client) {
            this.client = client;
            return this;
        }

        public void setProxy(String proxy) {
            this.proxy = proxy;
            setProxyStr();
            if (!hostname.equals("null") && !port.equals("-1")) {
                setClinetwithProxy();
            } else {
                setClinet();
            }
        }

        public void setProxyStr() {
            this.hostname = this.proxy.split(":")[0];
            this.port = this.proxy.split(":")[1];
        }

        public void setClinet() {
            this.client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .build();
        }

        public void setClinetwithProxy() {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.hostname, Integer.parseInt(this.port)));
            this.client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .proxy(proxy)
                    .build();
        }

        public Config build() {
            Config config = new Config();
//            config.setSession(this.session);
//            config.setProject(this.project);
            config.setDependencyGraphBuilder(this.dependencyGraphBuilder);
            config.setClassPaths(this.classPaths);
            config.setApiKeys(this.apiKeys);
            config.setOS(this.OS);
            config.setStopWhenSuccess(this.stopWhenSuccess);
            config.setEnableMultithreading(this.enableMultithreading);
            config.setNoExecution(this.noExecution);
            config.setMaxThreads(this.maxThreads);
            config.setClassThreads(this.classThreads);
            config.setMethodThreads(this.methodThreads);
            config.setTestNumber(this.testNumber);
            config.setMaxRounds(this.maxRounds);
            config.setMaxPromptTokens(this.maxPromptTokens);
            config.setMinErrorTokens(this.minErrorTokens);
            config.setModel(this.model);
            config.setTemperature(this.temperature);
            config.setTopP(this.topP);
            config.setFrequencyPenalty(this.frequencyPenalty);
            config.setPresencePenalty(this.presencePenalty);
            config.setTestOutput(this.testOutput);
            config.setTmpOutput(this.tmpOutput);
            config.setParseOutput(this.parseOutput);
            config.setErrorOutput(this.errorOutput);
            config.setClassMapPath(this.classMapPath);
            config.setProxy(this.proxy);
            config.setHostname(this.hostname);
            config.setPort(this.port);
            config.setClient(this.client);
            config.setLog(this.log);
            config.setMavenProject(this.mavenProject);
            config.setProject(this.project);
            config.setBasePath(this.basePath);
            return config;
        }
    }

    @Override
    public String toString() {
        return "Config{" +
                "dependencyGraphBuilder=" + dependencyGraphBuilder +
                ", classPaths=" + classPaths +
                ", apiKeys=" + Arrays.toString(apiKeys) +
                ", log=" + log +
                ", OS='" + OS + '\'' +
                ", stopWhenSuccess=" + stopWhenSuccess +
                ", noExecution=" + noExecution +
                ", enableMultithreading=" + enableMultithreading +
                ", maxThreads=" + maxThreads +
                ", classThreads=" + classThreads +
                ", methodThreads=" + methodThreads +
                ", testNumber=" + testNumber +
                ", maxRounds=" + maxRounds +
                ", maxPromptTokens=" + maxPromptTokens +
                ", minErrorTokens=" + minErrorTokens +
                ", model='" + model + '\'' +
                ", temperature=" + temperature +
                ", topP=" + topP +
                ", frequencyPenalty=" + frequencyPenalty +
                ", presencePenalty=" + presencePenalty +
                ", testOutput=" + testOutput +
                ", tmpOutput=" + tmpOutput +
                ", parseOutput=" + parseOutput +
                ", errorOutput=" + errorOutput +
                ", classMapPath=" + classMapPath +
                ", proxy='" + proxy + '\'' +
                ", hostname='" + hostname + '\'' +
                ", port='" + port + '\'' +
                ", client=" + client +
                ", basePath='" + basePath + '\'' +
                ", project=" + project +
                ", mavenProject=" + mavenProject +
                '}';
    }

    public String getRandomKey() {
        Random rand = new Random();
        if (apiKeys.length == 0) {
            throw new RuntimeException("apiKeys is null!");
        }
        String apiKey = apiKeys[rand.nextInt(apiKeys.length)];
        return apiKey;
    }
}
