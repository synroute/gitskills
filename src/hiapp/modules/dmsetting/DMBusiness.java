package hiapp.modules.dmsetting;
/** 
 * @author yangwentian 
 * @version 创建时间：2017年9月8日 下午2:05:42 
 * 类说明  用于存储业务信息
 */
public class DMBusiness {
	private int bizId;						//业务ID
	private String name;				    //业务名称
	private String desc;			        //描述
	private String ownerGroupId;		    //所属组
	private String ownerGroupName;		    //所属组名
	private int modeId;						//选择的外呼模式ID
	private String modeInfo;				//外呼模式详细
	private String configJson;	            //设置信息Json
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getOwnerGroupId() {
		return ownerGroupId;
	}
	public void setOwnerGroupId(String ownerGroupId) {
		this.ownerGroupId = ownerGroupId;
	}
	
	
	public int getBizId() {
		return bizId;
	}
	public void setBizId(int bizId) {
		this.bizId = bizId;
	}
	public int getModeId() {
		return modeId;
	}
	public void setModeId(int modeId) {
		this.modeId = modeId;
	}
	public String getConfigJson() {
		return configJson;
	}
	public void setConfigJson(String configJson) {
		this.configJson = configJson;
	}
	public String getOwnerGroupName() {
		return ownerGroupName;
	}
	public void setOwnerGroupName(String ownerGroupName) {
		this.ownerGroupName = ownerGroupName;
	}
	public String getModeInfo() {
		return modeInfo;
	}
	public void setModeInfo(String modeInfo) {
		this.modeInfo = modeInfo;
	}
}
