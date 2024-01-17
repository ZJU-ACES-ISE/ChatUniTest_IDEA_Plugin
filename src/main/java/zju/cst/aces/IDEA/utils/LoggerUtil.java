package zju.cst.aces.IDEA.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;

public class LoggerUtil {
    public static void info(Project project,String content){
        Notifications.Bus.notify(new Notification("Custom Notification Group",content, NotificationType.INFORMATION), project);
    }
    public static void error(Project project,String content){
        Notifications.Bus.notify(new Notification("Custom Notification Group",content, NotificationType.ERROR), project);
    }
    public static void warn(Project project,String content){
        Notifications.Bus.notify(new Notification("Custom Notification Group",content, NotificationType.WARNING), project);
    }
}
