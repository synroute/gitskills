package hiapp.modules.dmsetting;

/** 
 * @author liuhao
 * @version 创建时间：2017年9月8日 下午4:19:28 
 * 类说明 业务员结束码信息
 */
public class DMEndCode {
	//业务ID
	private int bizId;
	//结束码类型
	private String	endCodeType;
	//结束码
	private String	endCode;
	//描述
	private String	desc;
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
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getBizId() {
		return bizId;
	}
	public void setBizId(int bizId) {
		this.bizId = bizId;
	}
}
