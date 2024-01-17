package zju.cst.aces.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import zju.cst.aces.IDEA.runner.Task;
import com.intellij.openapi.vfs.VirtualFileManager;
import zju.cst.aces.IDEA.utils.FileUtil;


public class ModuleTestGeneration extends TestGeneration{
    public void generateModuleTest(AnActionEvent event){
        Module clickModule = LangDataKeys.MODULE.getData(event.getDataContext());
        init(ChatunitestPluginAction.IDEAProject,clickModule, ApplicationManager.getApplication());
        new Task(config).startProjectTask();
        //生成结束，刷新项目
        FileUtil.clearTestJavaFiles(ChatunitestPluginAction.tempTestJavaFiles);
    }
}
