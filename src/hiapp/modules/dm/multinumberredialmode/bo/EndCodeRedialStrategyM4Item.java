package hiapp.modules.dm.multinumberredialmode.bo;

public class EndCodeRedialStrategyM4Item {
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

    public Boolean getPhoneTypeDialFinished() {
        return phoneTypeDialFinished;
    }

    public void setPhoneTypeDialFinished(Boolean phoneTypeDialFinished) {
        this.phoneTypeDialFinished = phoneTypeDialFinished;
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

    public Boolean getPresetDial() {
        return presetDial;
    }

    public void setPresetDial(Boolean presetDial) {
        this.presetDial = presetDial;
    }


    String resultCodeType;
    String resultCode;
    String description;
    Boolean customerDialFinished;
    Boolean phoneTypeDialFinished;
    int redialDelayMinutes;
    int maxRedialNum;
    Boolean presetDial;
}