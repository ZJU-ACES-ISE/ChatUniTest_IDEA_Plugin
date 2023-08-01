/*
 * Created by JFormDesigner on Mon Jul 24 09:27:56 CST 2023
 */

package zju.cst.aces.Windows;

import zju.cst.aces.utils.ConnectUtil;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author 86138
 */
public class SettingWindow extends JPanel {
    public SettingWindow(Project project, ToolWindow toolWindow) {
        initComponents();
        initDefaultConfig();
    }

    public void fillPanelInfoByConfig() {
        apikey.setText(String.join(",", WindowConfig.apiKeys));
        hostname.setText(WindowConfig.hostname);
        port.setText(WindowConfig.port);
        testNumber.setText(String.valueOf(WindowConfig.testNumber));
        maxRounds.setText(String.valueOf(WindowConfig.maxRounds));
        minErrorTokens.setText(String.valueOf(WindowConfig.minErrorTokens));
        model.setSelectedIndex(WindowConfig.model_index);
        topP.setText(String.valueOf(WindowConfig.topP));
        temperature.setText(String.valueOf(WindowConfig.temperature));
        frequencyPenalty.setText(String.valueOf(WindowConfig.frequencyPenalty));
        presencePenalty.setText(String.valueOf(WindowConfig.presencePenalty));
        tmpOutput.setText(WindowConfig.tmpOutput);
        stopWhenSuccess.setSelected(WindowConfig.stopWhenSuccess);
        enableMultithreading.setSelected(WindowConfig.enableMultithreading);
        noExecution.setSelected(WindowConfig.noExecution);
        maxThreads.setText(String.valueOf(WindowConfig.maxThreads));
        testOutput.setText(WindowConfig.testOutput);
        maxPromptTokens.setText(String.valueOf(WindowConfig.maxPromptTokens));
    }

    @Override
    public String toString() {
        return "SettingWindow{" +
                "textfield=" + textfield.getText() +
                ", apikey=" + apikey.getText() +
                ", hostname=" + hostname.getText() +
                ", port=" + port.getText() +
                ", stopWhenSuccess=" + stopWhenSuccess.isSelected() +
                ", enableMultithreading=" + enableMultithreading.isSelected() +
                ", noExecution=" + noExecution.isSelected() +
                ", maxThreads=" + maxThreads.getText() +
                ", model=" + model.getSelectedItem() +
                ", testNumber=" + testNumber.getText() +
                ", maxRounds=" + maxRounds.getText() +
                ", minErrorTokens=" + minErrorTokens.getText() +
                ", maxPromptTokens=" + maxPromptTokens.getText() +
                ", topP=" + topP.getText() +
                ", temperature=" + temperature.getText() +
                ", frequencyPenalty=" + frequencyPenalty.getText() +
                ", presencePenalty=" + presencePenalty.getText() +
                ", tmpOutput=" + tmpOutput.getText() +
                ", testOutput=" + testOutput.getText() +
                '}';
    }

    private void confirm(ActionEvent event) {
        WindowConfig.apiKeys = apikey.getText().split(",");
        WindowConfig.hostname = hostname.getText();
        WindowConfig.port = (port.getText().equals("") ? null : (port.getText()));
        WindowConfig.testNumber = (testNumber.getText().equals("") ? WindowDefaultConfig.testNumber : Integer.parseInt(testNumber.getText()));
        WindowConfig.maxRounds = (maxRounds.getText().equals("") ? WindowDefaultConfig.maxRounds : Integer.parseInt(maxRounds.getText()));
        WindowConfig.minErrorTokens = (minErrorTokens.getText().equals("") ? WindowDefaultConfig.minErrorTokens : Integer.parseInt(minErrorTokens.getText()));
        WindowConfig.topP = (topP.getText().equals("") ? WindowDefaultConfig.topP : Integer.parseInt(topP.getText()));
        WindowConfig.temperature = (temperature.getText().equals("") ? WindowDefaultConfig.temperature : Double.parseDouble(temperature.getText()));
        WindowConfig.frequencyPenalty = (frequencyPenalty.getText().equals("") ? WindowDefaultConfig.frequencyPenalty : Integer.parseInt(frequencyPenalty.getText()));
        WindowConfig.presencePenalty = (presencePenalty.getText().equals("") ? WindowDefaultConfig.presencePenalty : Integer.parseInt(presencePenalty.getText()));
        WindowConfig.tmpOutput = (tmpOutput.getText().equals("") ? WindowDefaultConfig.tmpOutput : tmpOutput.getText());
        WindowConfig.testOutput = (testOutput.getText().equals("") ? WindowDefaultConfig.testOutput : testOutput.getText());
        WindowConfig.maxPromptTokens = (maxPromptTokens.getText().equals("") ? WindowDefaultConfig.maxPromptTokens : Integer.parseInt(maxPromptTokens.getText()));
        WindowConfig.model_index = model.getSelectedIndex();
        WindowConfig.maxThreads = (maxThreads.getText().equals("") ? WindowDefaultConfig.maxThreads : Integer.parseInt(maxThreads.getText()));
        WindowConfig.stopWhenSuccess = stopWhenSuccess.isSelected();
        WindowConfig.enableMultithreading = enableMultithreading.isSelected();
        WindowConfig.noExecution = noExecution.isSelected();
        fillPanelInfoByConfig();
    }

    private void initDefaultConfig() {
        testNumber.setText(String.valueOf(WindowDefaultConfig.testNumber));
        maxRounds.setText(String.valueOf(WindowDefaultConfig.maxRounds));
        minErrorTokens.setText(String.valueOf(WindowDefaultConfig.minErrorTokens));
        model.setSelectedIndex(WindowDefaultConfig.model_index);
        topP.setText(String.valueOf(WindowDefaultConfig.topP));
        temperature.setText(String.valueOf(WindowDefaultConfig.temperature));
        frequencyPenalty.setText(String.valueOf(WindowDefaultConfig.frequencyPenalty));
        presencePenalty.setText(String.valueOf(WindowDefaultConfig.presencePenalty));
        tmpOutput.setText(WindowDefaultConfig.tmpOutput);
        stopWhenSuccess.setSelected(WindowDefaultConfig.stopWhenSuccess);
        enableMultithreading.setSelected(WindowDefaultConfig.enableMultithreading);
        noExecution.setSelected(WindowDefaultConfig.noExecution);
        maxThreads.setText(String.valueOf(WindowDefaultConfig.maxThreads));
        testOutput.setText(WindowDefaultConfig.testOutput);
        maxPromptTokens.setText(String.valueOf(WindowDefaultConfig.maxPromptTokens));
    }

    private void setDefaultConfig(ActionEvent event) {
        initDefaultConfig();
        confirm(event);
    }

    private void testConnection(ActionEvent e) {
        Application application = ApplicationManager.getApplication();
        application.executeOnPooledThread(() -> {
            boolean isConnected = ConnectUtil.TestOpenApiConnection(apikey.getText(), hostname.getText(), port.getText());
            SwingUtilities.invokeLater(()->{
                testStatus.setText("");
                if (isConnected) {
                    testStatus.setForeground(Color.white);
                    testStatus.setText("connection successful");
                } else {
                    testStatus.setForeground(Color.red);
                    testStatus.setText("connection failed");
                }
            });
        });

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        textfield = new JLabel();
        apikey = new JTextField();
        label2 = new JLabel();
        label3 = new JLabel();
        hostname = new JTextField();
        label15 = new JLabel();
        port = new JTextField();
        testConnectionButton = new JButton();
        label9 = new JLabel();
        stopWhenSuccess = new JCheckBox();
        label4 = new JLabel();
        enableMultithreading = new JCheckBox();
        label16 = new JLabel();
        noExecution = new JCheckBox();
        testStatus = new JLabel();
        label17 = new JLabel();
        maxThreads = new JTextField();
        label11 = new JLabel();
        model = new JComboBox<>();
        label5 = new JLabel();
        testNumber = new JTextField();
        label10 = new JLabel();
        maxRounds = new JTextField();
        label6 = new JLabel();
        minErrorTokens = new JTextField();
        label18 = new JLabel();
        maxPromptTokens = new JTextField();
        label7 = new JLabel();
        topP = new JTextField();
        label14 = new JLabel();
        temperature = new JTextField();
        label12 = new JLabel();
        frequencyPenalty = new JTextField();
        label13 = new JLabel();
        presencePenalty = new JTextField();
        label8 = new JLabel();
        tmpOutput = new JTextField();
        label1 = new JLabel();
        testOutput = new JTextField();
        confirmButton = new JButton();
        button1 = new JButton();

        //======== this ========
        setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]",
                // rows
                "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]"));

        //---- textfield ----
        textfield.setText("Apikey");
        add(textfield, "cell 1 1");
        add(apikey, "cell 2 1 19 1");

        //---- label2 ----
        label2.setText("Proxy");
        add(label2, "cell 1 2");

        //---- label3 ----
        label3.setText("Host:");
        add(label3, "cell 2 2 2 1");
        add(hostname, "cell 4 2 4 1");

        //---- label15 ----
        label15.setText("Port:");
        add(label15, "cell 10 2");
        add(port, "cell 10 2");

        //---- testConnectionButton ----
        testConnectionButton.setText("test connection");
        testConnectionButton.addActionListener(e -> testConnection(e));
        add(testConnectionButton, "cell 16 2 5 1");

        //---- label9 ----
        label9.setText("stopWhenSuccess");
        add(label9, "cell 1 3");

        //---- stopWhenSuccess ----
        stopWhenSuccess.setSelected(true);
        add(stopWhenSuccess, "cell 2 3,aligny center,growy 0");

        //---- label4 ----
        label4.setText("enableMultithreading");
        add(label4, "cell 4 3,aligny center,growy 0");
        add(enableMultithreading, "cell 5 3,aligny center,growy 0");

        //---- label16 ----
        label16.setText("noExecution");
        add(label16, "cell 10 3,aligny center,growy 0");
        add(noExecution, "cell 10 3,aligny center,growy 0");
        add(testStatus, "cell 18 3,aligny top,growy 0");

        //---- label17 ----
        label17.setText("maxThreads");
        add(label17, "cell 1 4");
        add(maxThreads, "cell 2 4 3 1");

        //---- label11 ----
        label11.setText("Model");
        add(label11, "cell 10 4");

        //---- model ----
        model.setModel(new DefaultComboBoxModel<>(new String[]{
                "gpt-3.5-turbo",
                "gpt-4"
        }));
        add(model, "cell 13 4 8 1");

        //---- label5 ----
        label5.setText("Testnumber");
        add(label5, "cell 1 5");
        add(testNumber, "cell 2 5 3 1");

        //---- label10 ----
        label10.setText("MaxRounds");
        add(label10, "cell 10 5");
        add(maxRounds, "cell 13 5 8 1");

        //---- label6 ----
        label6.setText("MinErrorTokens");
        add(label6, "cell 1 6");
        add(minErrorTokens, "cell 2 6 3 1");

        //---- label18 ----
        label18.setText("MaxPromptTokens");
        add(label18, "cell 10 6");
        add(maxPromptTokens, "cell 13 6 8 1");

        //---- label7 ----
        label7.setText("topP");
        add(label7, "cell 1 7");
        add(topP, "cell 2 7 3 1");

        //---- label14 ----
        label14.setText("Temperature");
        add(label14, "cell 10 7");
        add(temperature, "cell 13 7 8 1");

        //---- label12 ----
        label12.setText("FrequencyPenalty");
        add(label12, "cell 1 9");
        add(frequencyPenalty, "cell 2 9 3 1");

        //---- label13 ----
        label13.setText("PresencePenalty");
        add(label13, "cell 10 9");
        add(presencePenalty, "cell 13 9 8 1");

        //---- label8 ----
        label8.setText("tmpOutput");
        add(label8, "cell 1 10");
        add(tmpOutput, "cell 2 10 3 1");

        //---- label1 ----
        label1.setText("testOutput");
        add(label1, "cell 10 10");
        add(testOutput, "cell 13 10 8 1");

        //---- confirmButton ----
        confirmButton.setText("Confirm");
        confirmButton.addActionListener(e -> confirm(e));
        add(confirmButton, "cell 4 13");

        //---- button1 ----
        button1.setText("Default");
        button1.addActionListener(e -> setDefaultConfig(e));
        add(button1, "cell 8 13 3 1");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel textfield;
    private JTextField apikey;
    private JLabel label2;
    private JLabel label3;
    private JTextField hostname;
    private JLabel label15;
    private JTextField port;
    private JButton testConnectionButton;
    private JLabel label9;
    private JCheckBox stopWhenSuccess;
    private JLabel label4;
    private JCheckBox enableMultithreading;
    private JLabel label16;
    private JCheckBox noExecution;
    private JLabel testStatus;
    private JLabel label17;
    private JTextField maxThreads;
    private JLabel label11;
    private JComboBox<String> model;
    private JLabel label5;
    private JTextField testNumber;
    private JLabel label10;
    private JTextField maxRounds;
    private JLabel label6;
    private JTextField minErrorTokens;
    private JLabel label18;
    private JTextField maxPromptTokens;
    private JLabel label7;
    private JTextField topP;
    private JLabel label14;
    private JTextField temperature;
    private JLabel label12;
    private JTextField frequencyPenalty;
    private JLabel label13;
    private JTextField presencePenalty;
    private JLabel label8;
    private JTextField tmpOutput;
    private JLabel label1;
    private JTextField testOutput;
    private JButton confirmButton;
    private JButton button1;

    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    public static void main(String[] args) {
    }


}
