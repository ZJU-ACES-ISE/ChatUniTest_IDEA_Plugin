package zju.cst.aces.config;

import java.util.Arrays;

public class ProjectConfigPersistence {
    public String[] apiKeys;
    public Boolean enableMultithreading;
    public String tmpOutput;
    public String testOutput;
    public Boolean stopWhenSuccess;
    public Boolean noExecution;
    public Integer maxThreads;
    public Integer testNumber;
    public Integer maxRounds;
    public Integer minErrorTokens;
    public Integer maxPromptTokens;
    public Integer model_index;
    public Double temperature;
    public Integer topP;
    public Integer frequencyPenalty;
    public Integer presencePenalty;
    public String hostname;
    public String port;
    public Boolean remind_regenerate;
    public Boolean remind_repair;
    public Boolean remind_compile;
    public Integer notifyRepair;

    public ProjectConfigPersistence() {
    }

    public String[] getApiKeys() {
        return apiKeys;
    }

    public void setApiKeys(String[] apiKeys) {
        this.apiKeys = apiKeys;
    }

    public Boolean getEnableMultithreading() {
        return enableMultithreading;
    }

    public void setEnableMultithreading(Boolean enableMultithreading) {
        this.enableMultithreading = enableMultithreading;
    }

    public String getTmpOutput() {
        return tmpOutput;
    }

    public void setTmpOutput(String tmpOutput) {
        this.tmpOutput = tmpOutput;
    }

    public String getTestOutput() {
        return testOutput;
    }

    public void setTestOutput(String testOutput) {
        this.testOutput = testOutput;
    }

    public Boolean getStopWhenSuccess() {
        return stopWhenSuccess;
    }

    public void setStopWhenSuccess(Boolean stopWhenSuccess) {
        this.stopWhenSuccess = stopWhenSuccess;
    }

    public Boolean getNoExecution() {
        return noExecution;
    }

    public void setNoExecution(Boolean noExecution) {
        this.noExecution = noExecution;
    }

    public Integer getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(Integer maxThreads) {
        this.maxThreads = maxThreads;
    }

    public Integer getTestNumber() {
        return testNumber;
    }

    public void setTestNumber(Integer testNumber) {
        this.testNumber = testNumber;
    }

    public Integer getMaxRounds() {
        return maxRounds;
    }

    public void setMaxRounds(Integer maxRounds) {
        this.maxRounds = maxRounds;
    }

    public Integer getMinErrorTokens() {
        return minErrorTokens;
    }

    public void setMinErrorTokens(Integer minErrorTokens) {
        this.minErrorTokens = minErrorTokens;
    }

    @Override
    public String toString() {
        return "ProjectConfigPersistence{" +
                "apiKeys=" + Arrays.toString(apiKeys) +
                ", enableMultithreading=" + enableMultithreading +
                ", tmpOutput='" + tmpOutput + '\'' +
                ", testOutput='" + testOutput + '\'' +
                ", stopWhenSuccess=" + stopWhenSuccess +
                ", noExecution=" + noExecution +
                ", maxThreads=" + maxThreads +
                ", testNumber=" + testNumber +
                ", maxRounds=" + maxRounds +
                ", minErrorTokens=" + minErrorTokens +
                ", maxPromptTokens=" + maxPromptTokens +
                ", model_index=" + model_index +
                ", temperature=" + temperature +
                ", topP=" + topP +
                ", frequencyPenalty=" + frequencyPenalty +
                ", presencePenalty=" + presencePenalty +
                ", hostname='" + hostname + '\'' +
                ", port='" + port + '\'' +
                ", remind_regenerate=" + remind_regenerate +
                ", remind_repair=" + remind_repair +
                ", remind_compile=" + remind_compile +
                ", notifyRepair=" + notifyRepair +
                '}';
    }

    public Integer getMaxPromptTokens() {
        return maxPromptTokens;
    }

    public void setMaxPromptTokens(Integer maxPromptTokens) {
        this.maxPromptTokens = maxPromptTokens;
    }

    public Integer getModel_index() {
        return model_index;
    }

    public void setModel_index(Integer model_index) {
        this.model_index = model_index;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getTopP() {
        return topP;
    }

    public void setTopP(Integer topP) {
        this.topP = topP;
    }

    public Integer getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public void setFrequencyPenalty(Integer frequencyPenalty) {
        this.frequencyPenalty = frequencyPenalty;
    }

    public Integer getPresencePenalty() {
        return presencePenalty;
    }

    public void setPresencePenalty(Integer presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Boolean getRemind_regenerate() {
        return remind_regenerate;
    }

    public void setRemind_regenerate(Boolean remind_regenerate) {
        this.remind_regenerate = remind_regenerate;
    }

    public Boolean getRemind_repair() {
        return remind_repair;
    }

    public void setRemind_repair(Boolean remind_repair) {
        this.remind_repair = remind_repair;
    }

    public Boolean getRemind_compile() {
        return remind_compile;
    }

    public void setRemind_compile(Boolean remind_compile) {
        this.remind_compile = remind_compile;
    }

    public Integer getNotifyRepair() {
        return notifyRepair;
    }

    public void setNotifyRepair(Integer notifyRepair) {
        this.notifyRepair = notifyRepair;
    }
}
