package zju.cst.aces.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import zju.cst.aces.IDEA.panel.SettingPanel;

public class ConfigSettingAction extends AnAction {
    /**
     * 全局配置持久化
     * @param event Carries information on the invocation place
     */
    @Override
    public void actionPerformed(AnActionEvent event) {
        SettingPanel settingPanel = new SettingPanel();
        settingPanel.show();
    }
}
