/*
 * Created by JFormDesigner on Mon Aug 28 16:48:10 CST 2023
 */

package zju.cst.aces.Windows.Panels;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import net.miginfocom.swing.MigLayout;
import zju.cst.aces.Windows.WindowConfig;
import zju.cst.aces.Windows.WindowDefaultConfig;
import zju.cst.aces.config.ConfigPersistence;
import zju.cst.aces.utils.ConnectUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Brainrain
 */
public class SettingPanel1 extends JFrame {
    public SettingPanel1() {
        initComponents();
        ConfigPersistence configPersistence = ApplicationManager.getApplication().getComponent(ConfigPersistence.class);
        ConfigPersistence.IdeaConfiguration ideaConfiguration=configPersistence.getState();
        String[] apiKeys_per = ideaConfiguration.apiKeys;
        System.out.println("apiKeys = " + apiKeys_per);
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
        apikey.setText(apiKeys_per!=null?String.join(",",apiKeys_per):"");
        hostname.setText(hostname_per!=null?hostname_per:"");
        port.setText(port_per!=null?port_per:"");
        testNumber.setText(testNumber_per!=null? String.valueOf(testNumber_per) : String.valueOf(WindowDefaultConfig.testNumber));
        maxRounds.setText(maxRounds_per!=null? String.valueOf(maxRounds_per) : String.valueOf(WindowDefaultConfig.maxRounds));
        minErrorTokens.setText(minErrorTokens_per!=null? String.valueOf(minErrorTokens_per) : String.valueOf(WindowDefaultConfig.minErrorTokens));
        model.setSelectedIndex(model_index_per!=null?model_index_per:WindowDefaultConfig.model_index);
        topP.setText(topP_per!=null? String.valueOf(topP_per) : String.valueOf(WindowDefaultConfig.topP));
        temperature.setText(temperature_per!=null? String.valueOf(temperature_per) : String.valueOf(WindowDefaultConfig.temperature));
        frequencyPenalty.setText(frequencyPenalty_per!=null? String.valueOf(frequencyPenalty_per) : String.valueOf(WindowDefaultConfig.frequencyPenalty));
        presencePenalty.setText(presencePenalty_per!=null? String.valueOf(presencePenalty_per) : String.valueOf(WindowDefaultConfig.presencePenalty));
        tmpOutput.setText(tmpOutput_per!=null?tmpOutput_per:WindowDefaultConfig.tmpOutput);
        stopWhenSuccess.setSelected(stopWhenSuccess_per!=null?stopWhenSuccess_per:WindowDefaultConfig.stopWhenSuccess);
        enableMultithreading.setSelected(enableMultithreading_per!=null?enableMultithreading_per:WindowDefaultConfig.enableMultithreading);
        noExecution.setSelected(noExecution_per!=null?noExecution_per:WindowDefaultConfig.noExecution);
        maxThreads.setText(maxThreads_per!=null? String.valueOf(maxThreads_per) : String.valueOf(WindowDefaultConfig.maxThreads));
        testOutput.setText(testOutput_per!=null?testOutput_per:WindowDefaultConfig.testOutput);
        maxPromptTokens.setText(maxPromptTokens_per!=null? String.valueOf(maxPromptTokens_per) : String.valueOf(WindowDefaultConfig.maxPromptTokens));
        confirm();
    }
    private void confirm() {

        ConfigPersistence configPersistence = ApplicationManager.getApplication().getComponent(ConfigPersistence.class);
        ConfigPersistence.IdeaConfiguration ideaConfiguration=configPersistence.getState();

        ideaConfiguration.apiKeys = apikey.getText().split(",");
        ideaConfiguration.hostname = hostname.getText();
        ideaConfiguration.port = (port.getText().equals("") ? null : (port.getText()));
        ideaConfiguration.testNumber = (testNumber.getText().equals("") ? WindowDefaultConfig.testNumber : Integer.parseInt(testNumber.getText()));
        ideaConfiguration.maxRounds = (maxRounds.getText().equals("") ? WindowDefaultConfig.maxRounds : Integer.parseInt(maxRounds.getText()));
        ideaConfiguration.minErrorTokens = (minErrorTokens.getText().equals("") ? WindowDefaultConfig.minErrorTokens : Integer.parseInt(minErrorTokens.getText()));
        ideaConfiguration.topP = (topP.getText().equals("") ? WindowDefaultConfig.topP : Integer.parseInt(topP.getText()));
        ideaConfiguration.temperature = (temperature.getText().equals("") ? WindowDefaultConfig.temperature : Double.parseDouble(temperature.getText()));
        ideaConfiguration.frequencyPenalty = (frequencyPenalty.getText().equals("") ? WindowDefaultConfig.frequencyPenalty : Integer.parseInt(frequencyPenalty.getText()));
        ideaConfiguration.presencePenalty = (presencePenalty.getText().equals("") ? WindowDefaultConfig.presencePenalty : Integer.parseInt(presencePenalty.getText()));
        ideaConfiguration.tmpOutput = (tmpOutput.getText().equals("") ? WindowDefaultConfig.tmpOutput : tmpOutput.getText());
        ideaConfiguration.testOutput = (testOutput.getText().equals("") ? WindowDefaultConfig.testOutput : testOutput.getText());
        ideaConfiguration.maxPromptTokens = (maxPromptTokens.getText().equals("") ? WindowDefaultConfig.maxPromptTokens : Integer.parseInt(maxPromptTokens.getText()));
        ideaConfiguration.model_index = model.getSelectedIndex();
        ideaConfiguration.maxThreads = (maxThreads.getText().equals("") ? WindowDefaultConfig.maxThreads : Integer.parseInt(maxThreads.getText()));
        ideaConfiguration.stopWhenSuccess = stopWhenSuccess.isSelected();
        ideaConfiguration.enableMultithreading = enableMultithreading.isSelected();
        ideaConfiguration.noExecution = noExecution.isSelected();
        configPersistence.loadState(ideaConfiguration);//更新持久化

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

    private void testConnection(ActionEvent e) {
            Application application = ApplicationManager.getApplication();
            application.executeOnPooledThread(() -> {
                boolean isConnected = ConnectUtil.testOpenApiConnection(apikey.getText().split(","), hostname.getText(), port.getText());
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

    private void modelMouseEnter(MouseEvent e) {
        // TODO add your code here
    }

    private void windowClosing(WindowEvent e) {
        confirm();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel11 = new JPanel();
        label17 = new JLabel();
        label18 = new JLabel();
        label19 = new JLabel();
        panel4 = new JPanel();
        label1 = new JLabel();
        apikey = new JTextField();
        label3 = new JLabel();
        hostname = new JTextField();
        label4 = new JLabel();
        port = new JTextField();
        testConnectionButton = new JButton();
        testStatus = new JLabel();
        panel5 = new JPanel();
        label2 = new JLabel();
        model = new JComboBox<>();
        label5 = new JLabel();
        topP = new JTextField();
        label6 = new JLabel();
        temperature = new JTextField();
        label9 = new JLabel();
        minErrorTokens = new JTextField();
        label10 = new JLabel();
        maxPromptTokens = new JTextField();
        label7 = new JLabel();
        frequencyPenalty = new JTextField();
        label8 = new JLabel();
        presencePenalty = new JTextField();
        panel6 = new JPanel();
        label14 = new JLabel();
        stopWhenSuccess = new JCheckBox();
        label15 = new JLabel();
        enableMultithreading = new JCheckBox();
        label16 = new JLabel();
        noExecution = new JCheckBox();
        separator1 = new JSeparator();
        label11 = new JLabel();
        maxThreads = new JTextField();
        label12 = new JLabel();
        testNumber = new JTextField();
        label13 = new JLabel();
        maxRounds = new JTextField();
        separator2 = new JSeparator();
        label21 = new JLabel();
        tmpOutput = new JTextField();
        label22 = new JLabel();
        testOutput = new JTextField();

        //======== this ========
        setIconImage(new ImageIcon(getClass().getResource("/icons/1693708608234.png")).getImage());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SettingPanel1.this.windowClosing(e);
            }
        });
        var contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "[fill]" +
            "[fill]",
            // rows
            "[]"));

        //======== panel11 ========
        {
            panel11.setBackground(new Color(230, 235, 240));
            panel11.setLayout(new MigLayout(
                "filly,hidemode 3,align left center",
                // columns
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
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]"));

            //---- label17 ----
            label17.setText("gpt connection");
            label17.setFont(label17.getFont().deriveFont(label17.getFont().getStyle() | Font.BOLD));
            label17.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mouseClickButton1(e);
                }
            });
            panel11.add(label17, "cell 0 3,alignx left,growx 0");

            //---- label18 ----
            label18.setText("gpt configuration");
            label18.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mouseClickButton2(e);
                }
            });
            panel11.add(label18, "cell 0 5,alignx left,growx 0");

            //---- label19 ----
            label19.setText("test settings");
            label19.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mouseClickButton3(e);
                }
            });
            panel11.add(label19, "cell 0 7,alignx left,growx 0");
        }
        contentPane.add(panel11, "cell 0 0,growy");

        //======== panel4 ========
        {
            panel4.setLayout(new MigLayout(
                "hidemode 3,align center center",
                // columns
                "[]" +
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
                "[]"));

            //---- label1 ----
            label1.setText("Apikeys:");
            label1.setToolTipText("(Not NULL) Apikeys of OpenAI,please split each apikey with ','");
            panel4.add(label1, "cell 4 2");
            panel4.add(apikey, "cell 5 2 9 1,growy");

            //---- label3 ----
            label3.setText("Host:");
            label3.setToolTipText("(Nullable) Host of your proxy");
            panel4.add(label3, "cell 4 3");
            panel4.add(hostname, "cell 5 3 9 1");

            //---- label4 ----
            label4.setText("Port:");
            label4.setToolTipText("(Nullable) Port of your proxy");
            panel4.add(label4, "cell 4 4");
            panel4.add(port, "cell 5 4 4 1");

            //---- testConnectionButton ----
            testConnectionButton.setText("test connection");
            testConnectionButton.addActionListener(e -> testConnection(e));
            panel4.add(testConnectionButton, "cell 4 6 3 1");
            panel4.add(testStatus, "cell 7 6 7 1");
        }
        contentPane.add(panel4, "cell 1 0");
        pack();
        setLocationRelativeTo(getOwner());

        //======== panel5 ========
        {
            panel5.setLayout(new MigLayout(
                "insets 0,hidemode 3,align center center,gap 5 5",
                // columns
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]",
                // rows
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]"));

            //---- label2 ----
            label2.setText("Model:");
            label2.setToolTipText("ChatGPT version");
            label2.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    modelMouseEnter(e);
                }
            });
            panel5.add(label2, "cell 0 0 2 1");

            //---- model ----
            model.setModel(new DefaultComboBoxModel<>(new String[] {
                "gpt-3.5-turbo",
                "gpt-4"
            }));
            panel5.add(model, "cell 2 0 3 1");

            //---- label5 ----
            label5.setText("TopP:");
            panel5.add(label5, "cell 0 1");
            panel5.add(topP, "cell 1 1 2 1");

            //---- label6 ----
            label6.setText("Temperature:");
            panel5.add(label6, "cell 3 1");
            panel5.add(temperature, "cell 4 1");

            //---- label9 ----
            label9.setText("MinErrorTokens:");
            panel5.add(label9, "cell 0 2 2 1");
            panel5.add(minErrorTokens, "cell 2 2 3 1");

            //---- label10 ----
            label10.setText("MaxPromptTokens:");
            panel5.add(label10, "cell 0 3 2 1");
            panel5.add(maxPromptTokens, "cell 2 3 3 1");

            //---- label7 ----
            label7.setText("FrequencyPenalty:");
            panel5.add(label7, "cell 0 4 2 1");
            panel5.add(frequencyPenalty, "cell 2 4 3 1");

            //---- label8 ----
            label8.setText("PresencePenalty:");
            panel5.add(label8, "cell 0 5 2 1");
            panel5.add(presencePenalty, "cell 2 5 3 1");
        }

        //======== panel6 ========
        {
            panel6.setLayout(new MigLayout(
                "insets 0,hidemode 3,align center center,gap 5 5",
                // columns
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]",
                // rows
                "[]" +
                "[]" +
                "[]" +
                "[fill]" +
                "[fill]" +
                "[]" +
                "[fill]" +
                "[]"));

            //---- label14 ----
            label14.setText("StopWhenSuccess");
            label14.setToolTipText("stop generate tests when successful");
            panel6.add(label14, "cell 0 0");

            //---- stopWhenSuccess ----
            stopWhenSuccess.setSelected(true);
            panel6.add(stopWhenSuccess, "cell 1 0,align center center,grow 0 0");

            //---- label15 ----
            label15.setText("EnableMultithreading");
            label15.setToolTipText("use multithreads");
            panel6.add(label15, "cell 2 0");
            panel6.add(enableMultithreading, "cell 3 0,align center center,grow 0 0");

            //---- label16 ----
            label16.setText("NoExecution");
            label16.setToolTipText("generate  but not execute the test");
            panel6.add(label16, "cell 0 1");
            panel6.add(noExecution, "cell 1 1,align center center,grow 0 0");
            panel6.add(separator1, "cell 0 2 4 1,gapx 0 0,gapy 10 10");

            //---- label11 ----
            label11.setText("MaxThreads:");
            label11.setToolTipText("max threads number when generate test");
            panel6.add(label11, "cell 0 3");
            panel6.add(maxThreads, "cell 1 3");

            //---- label12 ----
            label12.setText("TestNumber:");
            panel6.add(label12, "cell 2 3");
            panel6.add(testNumber, "cell 3 3");

            //---- label13 ----
            label13.setText("MaxRounds:");
            label13.setToolTipText("max round number when generate a test");
            panel6.add(label13, "cell 0 4");
            panel6.add(maxRounds, "cell 1 4");
            panel6.add(separator2, "cell 0 5 4 1,gapx 0 0,gapy 10 10");

            //---- label21 ----
            label21.setText("tmpOutput:");
            panel6.add(label21, "cell 0 6");
            panel6.add(tmpOutput, "cell 1 6 2 1");

            //---- label22 ----
            label22.setText("testOutput:");
            panel6.add(label22, "cell 0 7");
            panel6.add(testOutput, "cell 1 7 2 1");
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel11;
    private JLabel label17;
    private JLabel label18;
    private JLabel label19;
    private JPanel panel4;
    private JLabel label1;
    private JTextField apikey;
    private JLabel label3;
    private JTextField hostname;
    private JLabel label4;
    private JTextField port;
    private JButton testConnectionButton;
    private JLabel testStatus;
    private JPanel panel5;
    private JLabel label2;
    private JComboBox<String> model;
    private JLabel label5;
    private JTextField topP;
    private JLabel label6;
    private JTextField temperature;
    private JLabel label9;
    private JTextField minErrorTokens;
    private JLabel label10;
    private JTextField maxPromptTokens;
    private JLabel label7;
    private JTextField frequencyPenalty;
    private JLabel label8;
    private JTextField presencePenalty;
    private JPanel panel6;
    private JLabel label14;
    private JCheckBox stopWhenSuccess;
    private JLabel label15;
    private JCheckBox enableMultithreading;
    private JLabel label16;
    private JCheckBox noExecution;
    private JSeparator separator1;
    private JLabel label11;
    private JTextField maxThreads;
    private JLabel label12;
    private JTextField testNumber;
    private JLabel label13;
    private JTextField maxRounds;
    private JSeparator separator2;
    private JLabel label21;
    private JTextField tmpOutput;
    private JLabel label22;
    private JTextField testOutput;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
    private void mouseClickButton1(MouseEvent e) {
        SwingUtilities.invokeLater(() -> {
            label18.setFont(label18.getFont().deriveFont(Font.PLAIN)); // 恢复为正常字体
            label19.setFont(label19.getFont().deriveFont(Font.PLAIN)); // 恢复为正常字体
            label17.setFont(label17.getFont().deriveFont(Font.BOLD)); // 设置为粗体
            getContentPane().remove(panel5);
            getContentPane().remove(panel6);
            getContentPane().add(panel4, "cell 1 0");
            revalidate();
            repaint();
        });
    }

    private void mouseClickButton2(MouseEvent e) {
        SwingUtilities.invokeLater(() -> {
            label17.setFont(label17.getFont().deriveFont(Font.PLAIN)); // 恢复为正常字体
            label19.setFont(label19.getFont().deriveFont(Font.PLAIN)); // 恢复为正常字体
            label18.setFont(label18.getFont().deriveFont(Font.BOLD)); // 设置为粗体
            getContentPane().remove(panel4);
            getContentPane().remove(panel6);
            getContentPane().add(panel5, "cell 1 0");
            revalidate();
            repaint();
        });
    }

    private void mouseClickButton3(MouseEvent e) {
        SwingUtilities.invokeLater(() -> {
            label17.setFont(label17.getFont().deriveFont(Font.PLAIN)); // 恢复为正常字体
            label18.setFont(label18.getFont().deriveFont(Font.PLAIN)); // 恢复为正常字体
            label19.setFont(label19.getFont().deriveFont(Font.BOLD)); // 设置为粗体
            getContentPane().remove(panel4);
            getContentPane().remove(panel5);
            getContentPane().add(panel6, "cell 1 0");
            revalidate();
            repaint();
        });
    }

    public static void main(String[] args) {
        new SettingPanel1().setVisible(true);
    }
}
