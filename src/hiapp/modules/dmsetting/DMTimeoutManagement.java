package hiapp.modules.dmsetting;

public class DMTimeoutManagement {
	private int bizId;	//业务ID
	private int isEnable;	//是否启用
	private String timeOutConfig;	//超时提醒配置
	public int getIsEnable() {
		return isEnable;
	}
	public void setIsEnable(int isEnable) {
		this.isEnable = isEnable;
	}
	public String getTimeOutConfgi() {
		return timeOutConfig;
	}
	public void setTimeOutConfgi(String timeOutConfig) {
		this.timeOutConfig = timeOutConfig;
	}
	public int getBizId() {
		return bizId;
	}
	public void setBizId(int bizId) {
		this.bizId = bizId;
	}
}
