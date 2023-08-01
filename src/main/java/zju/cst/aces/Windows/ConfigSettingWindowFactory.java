package zju.cst.aces.Windows;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/*WindowFactory在plugin.xml中定义，进行关联*/
public class ConfigSettingWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        /*一套流程，固定套路，记得将project和toolwindow传入toolwindow的构造方法中*/
        /*ConfigSettingWindow configSettingWindow = new ConfigSettingWindow(project, toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(configSettingWindow.getFormPanel(), "", false);
        toolWindow.getContentManager().addContent(content);*/

        SettingWindow settingWindow = new SettingWindow(project,toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(settingWindow, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
