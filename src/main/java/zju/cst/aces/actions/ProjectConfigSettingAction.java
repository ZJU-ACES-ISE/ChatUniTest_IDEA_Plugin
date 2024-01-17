package zju.cst.aces.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import zju.cst.aces.IDEA.panel.ProjectSettingPanel;

public class ProjectConfigSettingAction extends AnAction {

    /**
     * 项目级别的配置，只针对当前项目有效
     * @param event Carries information on the invocation place
     */
    @Override
    public void actionPerformed(AnActionEvent event) {
        ProjectSettingPanel projectSettingPanel = new ProjectSettingPanel(event);
        projectSettingPanel.show();
    }
}
