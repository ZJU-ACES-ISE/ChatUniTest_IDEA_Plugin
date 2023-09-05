package zju.cst.aces.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import zju.cst.aces.Windows.Panels.SettingPanel;
import zju.cst.aces.Windows.Panels.SettingPanel1;

public class ConfigSettingAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        // TODO: insert action logic here
        System.out.println("setting ing");
        SettingPanel settingPanel = new SettingPanel();
        settingPanel.show();
    }
}
