package zju.cst.aces.IDEA.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import zju.cst.aces.actions.ChatunitestPluginAction;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileUtil {
    public static void refreshFolder(Path testOutput) {
        String folderPath = String.valueOf(testOutput);
        VirtualFile folder = LocalFileSystem.getInstance().refreshAndFindFileByPath(folderPath);
        if (folder != null && folder.isDirectory()) {
            folder.refresh(false, true);
        }
    }

    public static void clearTestJavaFiles(List<VirtualFile> tempTestJavaFiles) {
        Iterator<VirtualFile> iterator = tempTestJavaFiles.iterator();
        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationManager.getApplication().runWriteAction(() -> {
                while (iterator.hasNext()){
                    VirtualFile tempTestJavaFile=iterator.next();
                        try {
                            tempTestJavaFile.delete(ChatunitestPluginAction.IDEAProject);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                    }
                    iterator.remove();
                }
            });
        });
    }

}
