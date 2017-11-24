package hiapp.modules.dm.dbImport.bean;

public class ImportConfig {
	private int bizId;
	private int templateId;
	private int intervalTime;
	
	
	public int getIntervalTime() {
		return intervalTime;
	}
	public void setIntervalTime(int intervalTime) {
		this.intervalTime = intervalTime;
	}
	public int getBizId() {
		return bizId;
	}
	public void setBizId(int bizId) {
		this.bizId = bizId;
	}
	public int getTemplateId() {
		return templateId;
	}
	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}
}
