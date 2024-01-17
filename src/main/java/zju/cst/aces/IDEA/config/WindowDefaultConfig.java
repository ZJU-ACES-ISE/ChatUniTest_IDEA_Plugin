package zju.cst.aces.IDEA.config;

//默认的config信息
public class WindowDefaultConfig {
    public static String apiKeys="";
    public static boolean enableMultithreading=false;
    public static String tmpOutput="/tmp/chatunitest-info";
    public static String testOutput="chatunitest-tests";
    public static boolean stopWhenSuccess=true;
    public static boolean noExecution=false;
    public static int maxThreads=0;
    public static int testNumber=3;
    public static int maxRounds=3;
    public static int minErrorTokens=500;
    public static int maxPromptTokens=2600;
    public static int model_index=0;
    public static Double temperature=0.5;
    public static int topP=1;
    public static int frequencyPenalty=0;
    public static int presencePenalty=0;
    public static String proxy;
    public static String port="";
    public static String hostname="";
    public static String[] models={"gpt-3.5-turbo","gpt-3.5-turbo-1106","code-llama"};
    public static Boolean compileReminder=true;
    public static Boolean regenerateReminder=false;//不自动开始下一个round
    public static Boolean repairReminder=true;
    public static Integer notifyRepair=0;//默认Always notify repair process
}
