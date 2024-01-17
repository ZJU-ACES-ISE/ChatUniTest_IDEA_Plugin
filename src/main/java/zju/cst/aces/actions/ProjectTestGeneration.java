package zju.cst.aces.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.vfs.VirtualFileManager;
import zju.cst.aces.IDEA.runner.Task;
import zju.cst.aces.IDEA.utils.FileUtil;


public class ProjectTestGeneration extends TestGeneration{
    public void projectTestGenerate(AnActionEvent event) {
        ModuleManager moduleManager = ModuleManager.getInstance(ChatunitestPluginAction.IDEAProject);
        //用户选中的module
        Module[] modules = moduleManager.getModules();
        for (Module module : modules) {
            init(ChatunitestPluginAction.IDEAProject,module,ApplicationManager.getApplication());
            new Task(config).startProjectTask();
            //生成结束，刷新项目
            FileUtil.clearTestJavaFiles(ChatunitestPluginAction.tempTestJavaFiles);
        }
    }
}
