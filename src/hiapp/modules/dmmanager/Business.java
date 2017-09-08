package hiapp.modules.dmmanager;

public class Business {
	private Integer id;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
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
	public Integer getOwnergroup() {
		return ownergroup;
	}
	public void setOwnergroup(Integer ownergroup) {
		this.ownergroup = ownergroup;
	}
	public String getDetailSettingXml() {
		return detailSettingXml;
	}
	public void setDetailSettingXml(String detailSettingXml) {
		this.detailSettingXml = detailSettingXml;
	}
	public Integer getModeId() {
		return modeId;
	}
	public void setModeId(Integer modeId) {
		this.modeId = modeId;
	}
	public Integer getSubmodeId() {
		return submodeId;
	}
	public void setSubmodeId(Integer submodeId) {
		this.submodeId = submodeId;
	}
	private String name;
	private String description;
	private Integer ownergroup;
	private String detailSettingXml;
	private Integer modeId;
	private Integer submodeId;
}
