package zju.cst.aces.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFileManager;
import zju.cst.aces.IDEA.runner.Task;
import zju.cst.aces.IDEA.utils.FileUtil;

public class MethodTestGeneration extends TestGeneration{
    //methodName:"package.Class#methodName"
    public void methodTestGenerate(String className,String methodName, AnActionEvent event) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {

            Application application = ApplicationManager.getApplication();
            //用户选中的module
            Module clickModule = LangDataKeys.MODULE.getData(event.getDataContext());
            //用户选中的类名和方法名
            init(ChatunitestPluginAction.IDEAProject, clickModule, application);
            new Task(config).startMethodTask(className, methodName);
            //生成结束，刷新项目
            FileUtil.clearTestJavaFiles(ChatunitestPluginAction.tempTestJavaFiles);
        });
    }
}
