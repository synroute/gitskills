package hiapp.modules.dm.manualmode.timeoutpro;

/**
 * Created by shizhenshuang on 2018/6/14.
 */
public class TimeoutConfig {
    /*
    "timeoutConfig": {
		"Dimension": "超时维度",
		"Timekeeping": "超时计时单位", 天、小时、分钟
		"Duration": "超时时长",
		"TimeoutType": "超时提醒人",
		"TimeoutData": "超时提醒语"
		{"Dimension":"数据维度","Timekeeping":"小时","Duration":"2","TimeoutType":"拥有本数据池权限的人","TimeoutData":"你已超时"}
    * */
    private String Dimension;

    private String Timekeeping;

    private String Duration;

    private String TimeoutType;

    private String TimeoutData;

    public String getDimension() {
        return Dimension;
    }

    public void setDimension(String dimension) {
        Dimension = dimension;
    }

    public String getTimekeeping() {
        return Timekeeping;
    }

    public void setTimekeeping(String timekeeping) {
        Timekeeping = timekeeping;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getTimeoutType() {
        return TimeoutType;
    }

    public void setTimeoutType(String timeoutType) {
        TimeoutType = timeoutType;
    }

    public String getTimeoutData() {
        return TimeoutData;
    }

    public void setTimeoutData(String timeoutData) {
        TimeoutData = timeoutData;
    }

    @Override
    public String toString() {
        return "TimeoutConfig{" +
                "Dimension='" + Dimension + '\'' +
                ", Timekeeping='" + Timekeeping + '\'' +
                ", Duration='" + Duration + '\'' +
                ", TimeoutType='" + TimeoutType + '\'' +
                ", TimeoutData='" + TimeoutData + '\'' +
                '}';
    }
}
