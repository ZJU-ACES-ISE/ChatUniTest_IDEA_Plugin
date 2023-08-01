package zju.cst.aces.utils;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
    public static void refreshFolder(Path testOutput){
        String folderPath= String.valueOf(testOutput);
        VirtualFile folder = LocalFileSystem.getInstance().refreshAndFindFileByPath(folderPath);
        if(folder!=null&&folder.isDirectory()){
            folder.refresh(false,true);
        }
    }
}
