package zju.cst.aces.actions;

import com.google.gson.Gson;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;
import zju.cst.aces.Windows.WindowConfig;
import zju.cst.aces.Windows.WindowDefaultConfig;
import zju.cst.aces.config.Config;
import zju.cst.aces.config.ConfigPersistence;
import zju.cst.aces.dto.Message;
import zju.cst.aces.util.AskGPT;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

import static zju.cst.aces.runner.AbstractRunner.GSON;

public class PromptDefineAction extends AnAction {
    Config config;
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        loadPersistentConfig();
        Project project = event.getProject();
        String basePath = project.getBasePath();
        MavenProject mavenProject = MavenActionUtil.getMavenProject(event.getDataContext());
        init(project, basePath, mavenProject);
        ApplicationManager.getApplication().invokeLater(()->{
            CodeInputDialog dialog = new CodeInputDialog();
            dialog.show();
            if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
                String userInput = dialog.getCodeInput();
                AskGPT askGPT = new AskGPT(config);
                ArrayList<Message> messages = new ArrayList<>();
                Message message = new Message();
                message.setContent("Please evaluate if a unit test is essential for the given method. lf you decide it's necessary,generate the unit test specifications in JSON format, which should include a list of test caseswith their names. purposes, and input data, f a unit test is not required, please respond with'no' The Java source code is:\n" +
                        "\"\"\" \n" +
                        userInput +
                        "\"\"\",return pure JSON code only.");
                message.setRole("user");
                messages.add(message);
                Response response = askGPT.askChatGPT(messages);
                Map<String, Object> body = GSON.fromJson(response.body().charStream(), Map.class);
                String content = ((Map<String, String>) ((Map<String, Object>) ((ArrayList<?>) body.get("choices")).get(0)).get("message")).get("content");
                int startIndex=content.indexOf("{");
                int endIndex=content.lastIndexOf("}");
                if (startIndex != -1 && endIndex != -1) {
                    content = content.substring(startIndex, endIndex + 1);
                    // 现在 jsonContent 就包含了纯净的 JSON 代码
                }
                CodeInputDialog dialog1 = new CodeInputDialog();
                dialog1.setCodeInput(content);
                dialog1.show();
                if (dialog1.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
                    System.out.println("specification code:\n"+dialog1.getCodeInput());
                }

            }
        });

    }

    public void init(Project project, String basePath, MavenProject mavenProject) {
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
//                .others()
                .build();
    }

    public void loadPersistentConfig() {
        ConfigPersistence configPersistence = ApplicationManager.getApplication().getComponent(ConfigPersistence.class);
        ConfigPersistence.IdeaConfiguration ideaConfiguration = configPersistence.getState();
        String[] apiKeys_per = ideaConfiguration.apiKeys;
        String hostname_per = ideaConfiguration.hostname;
        String port_per = ideaConfiguration.port;
        Integer testNumber_per = ideaConfiguration.testNumber;
        Integer maxRounds_per = ideaConfiguration.maxRounds;
        Integer minErrorTokens_per = ideaConfiguration.minErrorTokens;
        Integer model_index_per = ideaConfiguration.model_index;
        Integer topP_per = ideaConfiguration.topP;
        Double temperature_per = ideaConfiguration.temperature;
        Integer frequencyPenalty_per = ideaConfiguration.frequencyPenalty;
        Integer presencePenalty_per = ideaConfiguration.presencePenalty;
        String tmpOutput_per = ideaConfiguration.tmpOutput;
        Boolean stopWhenSuccess_per = ideaConfiguration.stopWhenSuccess;
        Boolean enableMultithreading_per = ideaConfiguration.enableMultithreading;
        Boolean noExecution_per = ideaConfiguration.noExecution;
        Integer maxThreads_per = ideaConfiguration.maxThreads;
        String testOutput_per = ideaConfiguration.testOutput;
        Integer maxPromptTokens_per = ideaConfiguration.maxPromptTokens;
        WindowConfig.apiKeys = apiKeys_per;
        WindowConfig.hostname = hostname_per != null ? hostname_per : "";
        WindowConfig.port = port_per != null ? port_per : "";
        WindowConfig.testNumber = testNumber_per;
        WindowConfig.maxRounds = maxRounds_per;
        WindowConfig.minErrorTokens = minErrorTokens_per;
        WindowConfig.topP = topP_per;
        WindowConfig.temperature = temperature_per;
        WindowConfig.frequencyPenalty = frequencyPenalty_per;
        WindowConfig.presencePenalty = presencePenalty_per;
        WindowConfig.tmpOutput = tmpOutput_per;
        WindowConfig.testOutput = testOutput_per;
        WindowConfig.maxPromptTokens = maxPromptTokens_per;
        WindowConfig.model_index = model_index_per;
        WindowConfig.maxThreads = maxThreads_per;
        WindowConfig.stopWhenSuccess = stopWhenSuccess_per;
        WindowConfig.enableMultithreading = enableMultithreading_per;
        WindowConfig.noExecution = noExecution_per;
    }
}
class CodeInputDialog extends DialogWrapper {
    private JTextArea codeTextArea;
    private JScrollPane scrollPane; // 声明 scrollPane 为成员变量

    protected CodeInputDialog() {
        super(true);
        setTitle("Enter Code");

        codeTextArea = new JTextArea(30, 60);
        scrollPane = new JScrollPane(codeTextArea); // 初始化 scrollPane
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        setOKButtonText("Submit");
        setCancelButtonText("Cancel");
        setOKActionEnabled(true);
        setResizable(true);
        getPeer().getWindow().setSize(800, 600);
        init();
    }


    @NotNull
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
//        panel.add(new JLabel("Enter your code:"), BorderLayout.NORTH);

        // 将 codeTextArea 添加到面板
        panel.add(scrollPane, BorderLayout.CENTER); // 使用之前声明的 scrollPane

        return panel;
    }

    public String getCodeInput() {
        return codeTextArea.getText();
    }

    public void setCodeInput(String code) {
        codeTextArea.setText(code);
    }

    public JTextArea getCodeTextArea() {
        return codeTextArea;
    }

    public void setCodeTextArea(JTextArea codeTextArea) {
        this.codeTextArea = codeTextArea;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }
}