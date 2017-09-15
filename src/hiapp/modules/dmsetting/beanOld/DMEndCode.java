package hiapp.modules.dmsetting.beanOld;

public class DMEndCode {
	private int bizId;
	private String	endCodeType;
	private String	endCode;
	private String	description;
	public String getEndCodeType() {
		return endCodeType;
	}
	public void setEndCodeType(String endCodeType) {
		this.endCodeType = endCodeType;
	}
	public String getEndCode() {
		return endCode;
	}
	public void setEndCode(String endCode) {
		this.endCode = endCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getBizId() {
		return bizId;
	}
	public void setBizId(int bizId) {
		this.bizId = bizId;
	}
}
