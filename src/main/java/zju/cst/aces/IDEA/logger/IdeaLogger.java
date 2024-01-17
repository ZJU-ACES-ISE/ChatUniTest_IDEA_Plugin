package zju.cst.aces.IDEA.logger;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;
import zju.cst.aces.api.Logger;

public class IdeaLogger implements Logger {
    public Project project;
    public Application application;

    public IdeaLogger(Project project,Application application) {
        this.project = project;
        this.application=application;
    }

    @Override
    public void info(String s) {
        application.invokeLater(()->{
            Notifications.Bus.notify(new Notification("Custom Notification Group",s, NotificationType.INFORMATION), project);
        });
    }

    @Override
    public void warn(String s) {
        application.invokeLater(()-> {
            Notifications.Bus.notify(new Notification("Custom Notification Group", s, NotificationType.WARNING), project);
        });
    }

    @Override
    public void error(String s) {
        application.invokeLater(()-> {
            Notifications.Bus.notify(new Notification("Custom Notification Group", s, NotificationType.ERROR), project);
        });
    }

    @Override
    public void debug(String s) {

    }
}
