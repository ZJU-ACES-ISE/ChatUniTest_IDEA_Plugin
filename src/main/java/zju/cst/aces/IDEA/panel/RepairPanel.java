/*
 * Created by JFormDesigner on Wed Sep 06 16:56:50 CST 2023
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
public class RepairPanel extends JDialog {
    public static Integer repair_record=0;
    public RepairPanel() {
        initComponents();
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
        setTitle("Repair Setting");
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
                label1.setText("Test failed , attempt to repair the test");
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
                okButton.setText("Repair");
                okButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        repairTest(e);
                    }
                });
                buttonBar.add(okButton, "cell 13 0");

                //---- cancelButton ----
                cancelButton.setText("Cancel");
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

    public static Integer getRepair_record() {
        return repair_record;
    }

    public static void setRepair_record(Integer repair_record) {
        RepairPanel.repair_record = repair_record;
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

    public static void main(String[] args) {
        RepairPanel repairPanel = new RepairPanel();
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        repairPanel.okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("repair");
                if(repairPanel.checkBox1.isSelected()){
                    repair_record=1;
                }
                repairPanel.dispose();
                completableFuture.complete(1);
            }
        });
        repairPanel.cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("cancel");
                if(repairPanel.checkBox1.isSelected()){
                    repair_record=2;
                }
                repairPanel.dispose();
                completableFuture.complete(2);
            }
        });
        repairPanel.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                repairPanel.dispose();
                completableFuture.complete(2);
            }
        });
        repairPanel.setVisible(true);
        try {
            if(completableFuture.get()==1){
                System.out.println("repair");
                System.out.println("repair_record = " + repair_record);
            }
            else {
                System.out.println("do not repair");
                System.out.println("repair_record = " + repair_record);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
