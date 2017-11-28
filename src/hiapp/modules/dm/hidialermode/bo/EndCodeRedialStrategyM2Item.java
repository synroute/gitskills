package hiapp.modules.dm.hidialermode.bo;

public class EndCodeRedialStrategyM2Item {
    public String getResultCodeType() {
        return resultCodeType;
    }

    public void setResultCodeType(String resultCodeType) {
        this.resultCodeType = resultCodeType;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCustomerDialFinished() {
        return customerDialFinished;
    }

    public void setCustomerDialFinished(Boolean customerDialFinished) {
        this.customerDialFinished = customerDialFinished;
    }

    public int getRedialDelayMinutes() {
        return redialDelayMinutes;
    }

    public void setRedialDelayMinutes(int redialDelayMinutes) {
        this.redialDelayMinutes = redialDelayMinutes;
    }

    public int getMaxRedialNum() {
        return maxRedialNum;
    }

    public void setMaxRedialNum(int maxRedialNum) {
        this.maxRedialNum = maxRedialNum;
    }

    String resultCodeType;
    String resultCode;
    String description;
    Boolean customerDialFinished;
    int redialDelayMinutes;
    int maxRedialNum;
}