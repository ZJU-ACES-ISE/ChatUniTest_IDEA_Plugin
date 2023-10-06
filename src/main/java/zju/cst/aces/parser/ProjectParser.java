package zju.cst.aces.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.project.MavenProject;
import zju.cst.aces.config.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ProjectParser {

    public static final JavaParser parser = new JavaParser();
    public Path srcFolderPath;
    public Path outputPath;
    public Map<String, List<String>> classMap = new HashMap<>();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static Config config;

    public ProjectParser() {
        this.srcFolderPath = Paths.get(config.project.getBasePath(), "src", "main", "java");
        this.config = config;
        this.outputPath = config.getParseOutput();
        JavaSymbolSolver symbolSolver = getSymbolSolver(config.mavenProject);
        parser.getParserConfiguration().setSymbolResolver(symbolSolver);
    }

    public ProjectParser(Config config,Module module) {
        this.srcFolderPath = Paths.get(config.project.getBasePath(), "src", "main", "java");
        this.config = config;
        this.outputPath = config.getParseOutput();
//        JavaSymbolSolver symbolSolver = getSymbolSolver(config.mavenProject);
        JavaSymbolSolver symbolSolver=getSymbolSolver(config.project,module);
        parser.getParserConfiguration().setSymbolResolver(symbolSolver);
    }

    /**
     * Parse the project.
     */
    public void parse() {
        List<String> classPaths = new ArrayList<>();
        scanSourceDirectory(srcFolderPath.toFile(), classPaths);
        if (classPaths.isEmpty()) {
            throw new RuntimeException("No java file found in " + srcFolderPath);
        }
        for (String classPath : classPaths) {
            try {
                addClassMap(classPath);
                String packagePath = classPath.substring(srcFolderPath.toString().length() + 1);
                Path output = outputPath.resolve(packagePath).getParent();
                ClassParser classParser = new ClassParser(parser, output);
                classParser.extractClass(classPath);
            } catch (Exception e) {
                throw new RuntimeException("In ProjectParser.parse: " + e);
            }
        }
        exportClassMap();
    }

    public void addClassMap(String classPath) {
        String fullClassName = classPath.substring(srcFolderPath.toString().length() + 1)
                .replace(".java", "")
                .replace(File.separator, ".");

        String className = Paths.get(classPath).getFileName().toString().replace(".java", "");
        if (classMap.containsKey(className)) {
            classMap.get(className).add(fullClassName);
        } else {
            List<String> fullClassNames = new ArrayList<>();
            fullClassNames.add(fullClassName);
            classMap.put(className, fullClassNames);
        }
    }

    public void exportClassMap() {
        Path classMapPath = config.getClassMapPath();
        if (!Files.exists(classMapPath.getParent())) {
            try {
                Files.createDirectories(classMapPath.getParent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(classMapPath.toFile()), StandardCharsets.UTF_8)) {
            writer.write(GSON.toJson(classMap));
        } catch (Exception e) {
            throw new RuntimeException("In ProjectParser.exportNameMap: " + e);
        }
    }

    public static void scanSourceDirectory(File directory, List<String> classPaths) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        scanSourceDirectory(file, classPaths);
                    } else if (file.getName().endsWith(".java")) {
                        String classPath = file.getPath();
                        classPaths.add(classPath);
                    }
                }
            }
        }
    }

    //todo:改进getSymbolSolver方法，适配所有项目
    private JavaSymbolSolver getSymbolSolver(MavenProject mavenProject) {
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        //注意mavenProject.findDependencies方法没用
        for (String src : mavenProject.getSources()) {
            if (new File(src).exists()) {
                combinedTypeSolver.add(new JavaParserTypeSolver(src));
            }
        }
        List<MavenArtifact> dependencies = mavenProject.getDependencies();
        try {
            for (MavenArtifact dependency : dependencies) {
                String src = dependency.getPath();
                if (new File(src).exists()&&"jar".equals(dependency.getType())) {
                    combinedTypeSolver.add(new JarTypeSolver(new File(src)));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        return symbolSolver;
    }

    private JavaSymbolSolver getSymbolSolver(Project project, Module currentModule) {
        System.out.println("now projectParser_ClassPaths------------------------");
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        ArrayList<String> classPaths = new ArrayList<>();
        //针对所有项目类型
        Module[] modules = ModuleManager.getInstance(project).getModules();
            ModuleRootManager rootManager = ModuleRootManager.getInstance(currentModule);
            OrderEnumerator enumerator = rootManager.orderEntries();
            enumerator.recursively().forEachLibrary(library -> {
                VirtualFile[] files = library.getFiles(OrderRootType.CLASSES);
                for (VirtualFile file : files) {
                    // 获取 JAR 包的路径
                    String jarPath = file.getPresentableUrl();
                    classPaths.add(jarPath);
                }
                return true; // 继续遍历其他库
            });

        for (String classPath : classPaths) {
            try {
                System.out.println(classPath);
                combinedTypeSolver.add(new JarTypeSolver(new File(classPath)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (currentModule != null) {
            // 获取编译模块扩展
            CompilerModuleExtension extension = CompilerModuleExtension.getInstance(currentModule);
            // 获取输出目录
            VirtualFile outputDirectory = extension.getCompilerOutputPath();
            if (outputDirectory != null) {
                combinedTypeSolver.add(new JavaParserTypeSolver(outputDirectory.getPath()));
            }
        }
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        return symbolSolver;
    }

    /*private JavaSymbolSolver getSymbolSolver1() {
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        try {
            ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest(config.getSession().getProjectBuildingRequest() );
            buildingRequest.setProject(config.getProject());
            DependencyNode root = config.getDependencyGraphBuilder().buildDependencyGraph(buildingRequest, null);
            Set<DependencyNode> depSet = new HashSet<>();
            walkDep(root, depSet);
            for (DependencyNode dep : depSet) {
                try {
                    if (dep.getArtifact().getFile() != null) {
                        combinedTypeSolver.add(new JarTypeSolver(dep.getArtifact().getFile()));
                    }
                } catch (Exception e) {
                    config.getLog().warn(e.getMessage());
                    config.getLog().debug(e);
                }
            }
        } catch (Exception e) {
            config.getLog().warn(e.getMessage());
            config.getLog().debug(e);
        }
        for (String src : config.getProject().getCompileSourceRoots()) {
            if (new File(src).exists()) {
                combinedTypeSolver.add(new JavaParserTypeSolver(src));
            }
        }
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        return symbolSolver;
    }*/
    public static void walkDep(DependencyNode node, Set<DependencyNode> depSet) {
        depSet.add(node);
        for (DependencyNode dep : node.getChildren()) {
            walkDep(dep, depSet);
        }
    }
}
