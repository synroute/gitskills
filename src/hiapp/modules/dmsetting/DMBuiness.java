package hiapp.modules.dmsetting;
/** 
 * @author yangwentian 
 * @version 创建时间：2017年9月8日 下午2:05:42 
 * 类说明  用于存储业务信息
 */
public class DMBuiness {
	private int id;						//业务ID
	private String name;				//业务名称
	private String description;			//描述
	private String ownerGroupId;		//所属组
	private int subModeId;				//选择的业务类型ID
	private String detailConfigJson;	//详细设置信息Json
	private String modeSubmodeIdString; //拼接的字符串
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOwnerGroupId() {
		return ownerGroupId;
	}
	public void setOwnerGroupId(String ownerGroupId) {
		this.ownerGroupId = ownerGroupId;
	}
	public int getSubModeId() {
		return subModeId;
	}
	public void setSubModeId(int subModeId) {
		this.subModeId = subModeId;
	}
	public String getDetailConfigJson() {
		return detailConfigJson;
	}
	public void setDetailConfigJson(String detailConfigJson) {
		this.detailConfigJson = detailConfigJson;
	}
	public String getModeSubmodeIdString() {
		return modeSubmodeIdString;
	}
	public void setModeSubmodeIdString(String modeSubmodeIdString) {
		this.modeSubmodeIdString = modeSubmodeIdString;
	}
}
