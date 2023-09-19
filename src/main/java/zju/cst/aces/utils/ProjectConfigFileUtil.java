package zju.cst.aces.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import zju.cst.aces.Windows.WindowDefaultConfig;
import zju.cst.aces.config.ProjectConfigPersistence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

public class ProjectConfigFileUtil {
    public static void createConfigFile(String directoryPath,String fileName) throws IOException {
        File directory = new File(directoryPath);
        if(!directory.exists()){
            directory.mkdirs();
        }
        File file = new File(directory, fileName);
        if(!file.exists()){
            file.createNewFile();
            Document document = DocumentHelper.createDocument();
            Element rootElement = document.addElement("config");
            for (Field declaredField : WindowDefaultConfig.class.getDeclaredFields()) {
                try {
                    rootElement.addElement(declaredField.getName()).setText(String.valueOf(declaredField.get(null)));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            // 将XML写入文件
            try {
                XMLWriter writer = new XMLWriter(new FileWriter(file));
                writer.write(document);
                writer.close();
                System.out.println("配置数据已写入XML文件");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void modifyValue(String attribute,String value,File file) throws DocumentException, IOException {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(file);
        Element rootElement = document.getRootElement();
        Element targetElement = rootElement.element(attribute);
        if (targetElement != null) {
            targetElement.setText(value);
            // 将修改后的XML写回文件
            OutputFormat format = OutputFormat.createPrettyPrint(); // 格式化输出
            XMLWriter writer = new XMLWriter(new FileWriter(file), format);
            writer.write(document);
            writer.close();

            System.out.println("属性值已修改并保存到XML文件");
        } else {
            System.out.println("属性未找到");
        }
    }

    public static void modifyPersistence(ProjectConfigPersistence config, File file){
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(file);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        Element rootElement = document.getRootElement();
        for (Field field : config.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                String key=field.getName();
                if(key.equals("apiKeys")){
                    rootElement.element(key).setText(String.join(",",(String[])field.get(config)));
                    continue;
                }
                String value=String.valueOf(field.get(config));
                if(value==null){
                    value="";
                }
                //todo:后续统一命名问题
                if(key.equals("remind_regenerate")){
                    rootElement.element("regenerateReminder").setText(value);
                    continue;
                }
                if(key.equals("remind_repair")){
                    rootElement.element("repairReminder").setText(value);
                    continue;
                }
                if(key.equals("remind_compile")){
                    rootElement.element("compileReminder").setText(value);
                    continue;
                }
                /*if(key.equals("notifyRepair")){
                    rootElement.element("notifyRepair").setText(value);
                    continue;
                }*/
                rootElement.element(key).setText(value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        // 将修改后的XML写回文件
        OutputFormat format = OutputFormat.createPrettyPrint(); // 格式化输出
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(new FileWriter(file), format);
            writer.write(document);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ProjectConfigPersistence loadProjectConfig(String xmlPath){
        File xmlFile = new File(xmlPath);
        if(xmlFile.exists()){
            try {
                // 创建一个SAXReader对象来解析XML文件
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(xmlFile);

                // 获取XML中的根元素
                Element rootElement = document.getRootElement();

                // 创建一个ProjectConfigPersistence对象来存储读取的数据
                ProjectConfigPersistence config = new ProjectConfigPersistence();

                // 读取并映射XML中的属性到对象的属性
                config.setApiKeys(rootElement.elementText("apiKeys").split(","));
                config.setEnableMultithreading(Boolean.parseBoolean(rootElement.elementText("enableMultithreading")));
                config.setTmpOutput(rootElement.elementText("tmpOutput"));
                config.setTestOutput(rootElement.elementText("testOutput"));
                config.setStopWhenSuccess(Boolean.parseBoolean(rootElement.elementText("stopWhenSuccess")));
                config.setNoExecution(Boolean.parseBoolean(rootElement.elementText("noExecution")));
                config.setMaxThreads(Integer.parseInt(rootElement.elementText("maxThreads")));
                config.setTestNumber(Integer.parseInt(rootElement.elementText("testNumber")));
                config.setMaxRounds(Integer.parseInt(rootElement.elementText("maxRounds")));
                config.setMinErrorTokens(Integer.parseInt(rootElement.elementText("minErrorTokens")));
                config.setMaxPromptTokens(Integer.parseInt(rootElement.elementText("maxPromptTokens")));
                config.setModel_index(Integer.parseInt(rootElement.elementText("model_index")));
                config.setTemperature(Double.parseDouble(rootElement.elementText("temperature")));
                config.setTopP(Integer.parseInt(rootElement.elementText("topP")));
                config.setFrequencyPenalty(Integer.parseInt(rootElement.elementText("frequencyPenalty")));
                config.setPresencePenalty(Integer.parseInt(rootElement.elementText("presencePenalty")));
                config.setHostname(rootElement.elementText("hostname"));
                config.setPort(rootElement.elementText("port"));
                config.setRemind_regenerate(Boolean.parseBoolean(rootElement.elementText("regenerateReminder")));
                config.setRemind_repair(Boolean.parseBoolean(rootElement.elementText("repairReminder")));
                config.setRemind_compile(Boolean.parseBoolean(rootElement.elementText("compileReminder")));
                config.setNotifyRepair(Integer.parseInt(rootElement.elementText("notifyRepair")));
                return config;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        File file = new File("D:\\idea_plugin\\test_plugin\\.idea\\test_plugin_per.xml");
        ProjectConfigPersistence config = new ProjectConfigPersistence();
        config.setApiKeys(new String[]{"123", "456"});
        modifyPersistence(config,file);
    }
}
