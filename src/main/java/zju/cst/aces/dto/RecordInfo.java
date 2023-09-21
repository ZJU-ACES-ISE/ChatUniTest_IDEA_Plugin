package zju.cst.aces.dto;

public class RecordInfo {
    private String specification;
    private String prompt;
    private String resultTest;
    private String createTime;
    private String methodInfo;

    public RecordInfo() {
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getResultTest() {
        return resultTest;
    }

    public void setResultTest(String resultTest) {
        this.resultTest = resultTest;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMethodInfo() {
        return methodInfo;
    }

    public void setMethodInfo(String methodInfo) {
        this.methodInfo = methodInfo;
    }
}