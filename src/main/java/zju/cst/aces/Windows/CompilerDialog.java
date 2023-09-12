package zju.cst.aces.Windows;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class CompilerDialog extends DialogWrapper {
    public CompilerDialog(){
        super(true);
        init();
        setTitle("Insure the project has been compiled");
    }
    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel=new JPanel(new FlowLayout());
        JButton skipButton = new JButton("Skip");
        JButton compileButton = new JButton("Compile");
        skipButton.addActionListener(e -> doCancelAction());
        compileButton.addActionListener(e -> doOKAction());
        panel.add(skipButton);
        panel.add(compileButton);
        return panel;
    }

    public static void main(String[] args) {
        CompilerDialog compilerDialog = new CompilerDialog();
        compilerDialog.show();
    }
}
