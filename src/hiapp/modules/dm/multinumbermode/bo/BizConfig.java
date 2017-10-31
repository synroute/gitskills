package hiapp.modules.dm.multinumbermode.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BizConfig {

    //"PermissionCallTime":"[{\"TimeStart\":\"07:00:00\",\"TimeEnd\":\"07:15:00\"}]"}

    private String ServiceNo;
    private String DialPrefix;
    private String BusinessID;
    private String IVRScript;
    private String MaxRingCount;
    private String DialRatio;
    private String MaxReadyAgentCount;
    private String MaxCallingCount;
    private String DialMode;
    private String TimeUnitLong;
    private String MaxCountPerTimeUnit;
    private List<Map<String, String>> PermissionCallTime = new ArrayList<Map<String, String>>();

    public void setServiceNo(String ServiceNo) {
        this.ServiceNo = ServiceNo;
    }
    public String getServiceNo() {
        return ServiceNo;
    }

    public void setDialPrefix(String DialPrefix) {
        this.DialPrefix = DialPrefix;
    }
    public String getDialPrefix() {
        return DialPrefix;
    }

    public void setBusinessID(String BusinessID) {
        this.BusinessID = BusinessID;
    }
    public String getBusinessID() {
        return BusinessID;
    }

    public void setIVRScript(String IVRScript) {
        this.IVRScript = IVRScript;
    }
    public String getIVRScript() {
        return IVRScript;
    }

    public void setMaxRingCount(String MaxRingCount) {
        this.MaxRingCount = MaxRingCount;
    }
    public String getMaxRingCount() {
        return MaxRingCount;
    }

    public void setDialRatio(String DialRatio) {
        this.DialRatio = DialRatio;
    }
    public String getDialRatio() {
        return DialRatio;
    }

    public void setMaxReadyAgentCount(String MaxReadyAgentCount) {
        this.MaxReadyAgentCount = MaxReadyAgentCount;
    }
    public String getMaxReadyAgentCount() {
        return MaxReadyAgentCount;
    }

    public void setMaxCallingCount(String MaxCallingCount) {
        this.MaxCallingCount = MaxCallingCount;
    }
    public String getMaxCallingCount() {
        return MaxCallingCount;
    }

    public void setDialMode(String DialMode) {
        this.DialMode = DialMode;
    }
    public String getDialMode() {
        return DialMode;
    }

    public void setTimeUnitLong(String TimeUnitLong) {
        this.TimeUnitLong = TimeUnitLong;
    }
    public String getTimeUnitLong() {
        return TimeUnitLong;
    }

    public void setMaxCountPerTimeUnit(String MaxCountPerTimeUnit) {
        this.MaxCountPerTimeUnit = MaxCountPerTimeUnit;
    }
    public String getMaxCountPerTimeUnit() {
        return MaxCountPerTimeUnit;
    }

    public void setPermissionCallTime(Map<String, String> map) {
        this.PermissionCallTime.add(map);
    }

    public List<Map<String, String>> getPermissionCallTime() {
        return PermissionCallTime;
    }

}
