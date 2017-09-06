package hiapp.modules.dmsetting.data;

public class DMBusiness {
	private int id;
	private String name;
	private String description;
	private int ownerGroupId;
	private int modeId;
	private int subModeId;
	private String detailSettingXml;
	private String modeSubmodeIdString;
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
	public int getOwnerGroupId() {
		return ownerGroupId;
	}
	public void setOwnerGroupId(int ownerGroupId) {
		this.ownerGroupId = ownerGroupId;
	}
	public String getDetailSettingXml() {
		return detailSettingXml;
	}
	public void setDetailSettingXml(String detailSettingXml) {
		this.detailSettingXml = detailSettingXml;
	}
	public String getModeSubmodeIdString() {
		return modeSubmodeIdString;
	}
	public void setModeSubmodeIdString(String modeSubmodeIdString) {
		this.modeSubmodeIdString = modeSubmodeIdString;
	}
	public int getModeId() {
		return modeId;
	}
	public void setModeId(int modeId) {
		this.modeId = modeId;
	}
	public int getSubModeId() {
		return subModeId;
	}
	public void setSubModeId(int subModeId) {
		this.subModeId = subModeId;
	}
}
