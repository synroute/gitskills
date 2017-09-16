package hiapp.modules.dmsetting.result;
/** 
 * @author yangwentian 
 * @version 创建时间：2017年9月16日 下午5:32:14 
 * 类说明 
 */
public class DMBizOutboundSetting {

	private String endCodeType;
	private String endCode;
	private String endCodedescription;
	private String redialStateName;
	public String getEndCode() {
		return endCode;
	}
	public void setEndCode(String endCode) {
		this.endCode = endCode;
	}
	public String getEndCodeType() {
		return endCodeType;
	}
	public void setEndCodeType(String endCodeType) {
		this.endCodeType = endCodeType;
	}
	public String getEndCodedescription() {
		return endCodedescription;
	}
	public void setEndCodedescription(String endCodedescription) {
		this.endCodedescription = endCodedescription;
	}
	public String getRedialStateName() {
		return redialStateName;
	}
	public void setRedialStateName(String redialStateName) {
		this.redialStateName = redialStateName;
	}
}
