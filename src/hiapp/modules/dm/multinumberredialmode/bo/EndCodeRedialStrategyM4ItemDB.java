package hiapp.modules.dm.multinumberredialmode.bo;

public class EndCodeRedialStrategyM4ItemDB {
    String EndCodeType;
    String EndCode;
    String description;
    String IsCustStop;
    String IsPhoneStop;
    String RedialMinutes;
    String RedialCount;
    String isPresetDial;
    int SortNum;

    public String getEndCodeType() {
        return EndCodeType;
    }

    public void setEndCodeType(String endCodeType) {
        EndCodeType = endCodeType;
    }

    public String getEndCode() {
        return EndCode;
    }

    public void setEndCode(String endCode) {
        EndCode = endCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsCustStop() {
        return IsCustStop;
    }

    public void setIsCustStop(String isCustStop) {
        IsCustStop = isCustStop;
    }

    public String getIsPhoneStop() {
        return IsPhoneStop;
    }

    public void setIsPhoneStop(String isPhoneStop) {
        IsPhoneStop = isPhoneStop;
    }

    public String getRedialMinutes() {
        return RedialMinutes;
    }

    public void setRedialMinutes(String redialMinutes) {
        RedialMinutes = redialMinutes;
    }

    public String getRedialCount() {
        return RedialCount;
    }

    public void setRedialCount(String redialCount) {
        RedialCount = redialCount;
    }

    public String getPresetDial() {
        return isPresetDial;
    }

    public void setPresetDial(String isPresetDial) {
        this.isPresetDial = isPresetDial;
    }

    public int  getSortNum() {
        return SortNum;
    }

    public void setSortNum(int sortNum) {
        SortNum = sortNum;
    }
}