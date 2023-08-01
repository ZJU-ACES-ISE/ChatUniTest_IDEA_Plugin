package zju.cst.aces.Windows;

import java.nio.file.Path;

//默认的config信息
public class WindowDefaultConfig {
    public static boolean enableMultithreading=false;
    public static String tmpOutput="/tmp/chatunitest-info";
    public static String testOutput="chatunitest";
    public static boolean stopWhenSuccess=true;
    public static boolean noExecution=false;
    public static int maxThreads=0;
    public static int testNumber=5;
    public static int maxRounds=5;
    public static int minErrorTokens=500;
    public static int maxPromptTokens=2600;
    public static int model_index=0;
    public static Double temperature=0.5;
    public static int topP=1;
    public static int frequencyPenalty=0;
    public static int presencePenalty=0;
    public static String proxy;
    public static String[] models={"gpt-3.5-turbo","gpt-4"};
}
