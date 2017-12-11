package hiapp.modules.dm.multinumberredialmode.bo;

import java.util.Date;

public class PhoneDialInfo {
    String PhoneNumber;
    Date   LastDialTime;
    int    CausePresetDialCount;
    int    DialCount;
    Date   presetDialTime;

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public Date getLastDialTime() {
        return LastDialTime;
    }

    public void setLastDialTime(Date lastDialTime) {
        LastDialTime = lastDialTime;
    }

    public int getCausePresetDialCount() {
        return CausePresetDialCount;
    }

    public void setCausePresetDialCount(int causePresetDialCount) {
        CausePresetDialCount = causePresetDialCount;
    }

    public int getDialCount() {
        return DialCount;
    }

    public void setDialCount(int dialCount) {
        DialCount = dialCount;
    }

    public Date getPresetDialTime() {
        return presetDialTime;
    }

    public void setPresetDialTime(Date presetDialTime) {
        this.presetDialTime = presetDialTime;
    }

    public PhoneDialInfo clone() {
        PhoneDialInfo cloneItem = new PhoneDialInfo();
        cloneItem.setPhoneNumber(PhoneNumber);
        cloneItem.setLastDialTime(LastDialTime);
        cloneItem.setCausePresetDialCount(CausePresetDialCount);
        cloneItem.setDialCount(DialCount);
        return cloneItem;
    }
}
