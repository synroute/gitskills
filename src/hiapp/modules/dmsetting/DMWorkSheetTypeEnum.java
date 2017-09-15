package hiapp.modules.dmsetting;
/** 
 * @author yangwentian 
 * @version 创建时间：2017年9月8日 下午3:54:41 
 * 类说明  工作表类型枚举
 */
public enum DMWorkSheetTypeEnum {
	WSTDM_SYSTEM(1,"系统型"),
	WSTDM_MIDDLE(2,"中间型"),
	WSTDM_USERDEFINE(3,"用户自定义型");
	
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
