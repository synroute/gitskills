package hiapp.modules.dmsetting;

public enum DMWorkSheetTypeEnum {
	WSTDM_CUSTOMERIMPORT(1,"客户信息导入表"),
	WSTDM_RESULT(2,"结果表"),
	WSTDM_PRESETTIME(3,"预约表"),
	WSTDM_USERDEFINE(4,"用户定义"),
	WSTDM_DATAPOOL(5,"数据池记录表"),
	WSTDM_DATAPOOLORE(6,"数据池记录操作表"),
	WSTDM_DATAM3(7,"单号码重拨模式共享数据状态表"),
	WSTDM_DATAM3_HIS(8,"单号码重拨模式共享数据状态历史表");
	
	private int code;
    private String type;  
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
    private DMWorkSheetTypeEnum(int code,String type) {
    	this.code=code;
    	this.type=type;
    }  
}
