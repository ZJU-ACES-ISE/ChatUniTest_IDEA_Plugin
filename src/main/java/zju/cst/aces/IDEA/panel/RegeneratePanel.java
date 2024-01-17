/*
 * Created by JFormDesigner on Wed Sep 06 23:06:49 CST 2023
 */

package zju.cst.aces.IDEA.panel;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Brainrain
 */
public class RegeneratePanel extends JDialog {
    public static Integer regenerate_record=0;

    public RegeneratePanel() {
        initComponents();
    }

    public static Integer getRegenerate_record() {
        return regenerate_record;
    }

    public static void setRegenerate_record(Integer regenerate_record) {
        RegeneratePanel.regenerate_record = regenerate_record;
    }

    public JPanel getDialogPane() {
        return dialogPane;
    }

    public void setDialogPane(JPanel dialogPane) {
        this.dialogPane = dialogPane;
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public void setContentPanel(JPanel contentPanel) {
        this.contentPanel = contentPanel;
    }

    public JLabel getLabel1() {
        return label1;
    }

    public void setLabel1(JLabel label1) {
        this.label1 = label1;
    }

    public JPanel getButtonBar() {
        return buttonBar;
    }

    public void setButtonBar(JPanel buttonBar) {
        this.buttonBar = buttonBar;
    }



    public JCheckBox getCheckBox1() {
        return checkBox1;
    }

    public void setCheckBox1(JCheckBox checkBox1) {
        this.checkBox1 = checkBox1;
    }

    public JButton getOkButton() {
        return okButton;
    }

    public void setOkButton(JButton okButton) {
        this.okButton = okButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public void setCancelButton(JButton cancelButton) {
        this.cancelButton = cancelButton;
    }

    private void repairTest(MouseEvent e) {
    }

    private void cancelRepair(MouseEvent e) {
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        buttonBar = new JPanel();
        checkBox1 = new JCheckBox();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setTitle("Question");
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new MigLayout(
                    "insets dialog,hidemode 3",
                    // columns
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
                    "[]"));

                //---- label1 ----
                label1.setText("Test failed , start next round generation");
                label1.setFont(label1.getFont().deriveFont(label1.getFont().getStyle() | Font.BOLD, 13f));
                contentPanel.add(label1, "cell 1 2");
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setLayout(new MigLayout(
                    "insets dialog,alignx right",
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
                    "[button,fill]" +
                    "[button,fill]",
                    // rows
                    null));

                //---- checkBox1 ----
                checkBox1.setText("remember");
                buttonBar.add(checkBox1, "cell 1 0,align left center,grow 0 0");

                //---- okButton ----
                okButton.setText("Next");
                okButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        repairTest(e);
                    }
                });
                buttonBar.add(okButton, "cell 13 0");

                //---- cancelButton ----
                cancelButton.setText("Stop");
                cancelButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        cancelRepair(e);
                    }
                });
                buttonBar.add(cancelButton, "cell 14 0");
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private JPanel buttonBar;
    private JCheckBox checkBox1;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
    public static void main(String[] args) {
        RegeneratePanel regeneratePanel = new RegeneratePanel();
        CompletableFuture<Integer> completableFuture=new CompletableFuture<Integer>();
        regeneratePanel.okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("remember = " + regeneratePanel.checkBox1.isSelected());
                if(regeneratePanel.checkBox1.isSelected()){
                    regenerate_record=1;//标志着记录了第二个按钮，regenerate
                }
                regeneratePanel.dispose();
                completableFuture.complete(1);
            }
        });
        regeneratePanel.cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("regeneratePanel = " + regeneratePanel.checkBox1.isSelected());
                if(regeneratePanel.checkBox1.isSelected()){
                    regenerate_record=2;//标志着记录了第二个按钮，stop
                }
                regeneratePanel.dispose();
                completableFuture.complete(2);
            }
        });
        regeneratePanel.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                regeneratePanel.dispose();
                completableFuture.complete(2);
            }
        });
        regeneratePanel.setVisible(true);
        try {
            if(completableFuture.get()==1){
                System.out.println("go on doing");
            }
            else {
                System.out.println("stop");
            }
            System.out.println("regenerate_record = " + regenerate_record);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
