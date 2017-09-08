package hiapp.modules.dmsetting;
/** 
 * @author liuhao 
 * @version 创建时间：2017年9月8日 下午02:15:42 
 * 类说明  用于存储结束码信息
 */
public class DMEndCode {
	//结束码类型
	private String	endCodeType;
	//结束码
	private String	endCode;
	//描述
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
}
