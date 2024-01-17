package zju.cst.aces.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import zju.cst.aces.api.impl.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class ProjectParseGeneration extends TestGeneration{
    public void parseProject(){
        ModuleManager moduleManager = ModuleManager.getInstance(ChatunitestPluginAction.IDEAProject);
        Module[] modules = moduleManager.getModules();
        for (Module module : modules) {
            init(ChatunitestPluginAction.IDEAProject,module, ApplicationManager.getApplication());
            new Parser(config).process();
        }
    }
}
