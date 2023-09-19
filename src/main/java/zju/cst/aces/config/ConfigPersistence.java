package zju.cst.aces.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "ConfigPersistence",
        storages = {@Storage("ConfigPersistence_specification.xml")}
)
public class ConfigPersistence implements PersistentStateComponent<ConfigPersistence.IdeaConfiguration> {
    private IdeaConfiguration myConfig = new IdeaConfiguration();

    @Override
    public @Nullable ConfigPersistence.IdeaConfiguration getState() {
        return myConfig;
    }

    @Override
    public void loadState(@NotNull IdeaConfiguration ideaConfiguration) {
        myConfig = ideaConfiguration;
    }


    public static class IdeaConfiguration {
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
        public Boolean test_specification;
    }
}
