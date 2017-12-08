package hiapp.modules.dmsetting;
/** 
 * @author yangwentian 
 * @version 创建时间：2017年9月8日 下午3:54:41 
 * 类说明  工作表类型枚举
 */
public enum DMWorkSheetTypeEnum {
	WSTDM_IMPORT(1,"导入表"),
	WSTDM_RESULT(2,"结果表"),
	WSTDM_PRESET(3,"预约表"),
	WSTDM_POOL(4,"数据池记录表"),
	WSTDM_POOLORE(5,"数据池操作记录表"),
	WSTDM_SHARE(6,"共享表"),
	WSTDM_SHAREHISTROY(7,"共享历史表"),
	WSTDM_CUSTOM(8,"用户自定义表");
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
