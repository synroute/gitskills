package hiapp.modules.dmmanager;

public class Template {
	private Integer id;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getTemPlateId() {
		return temPlateId;
	}
	public void setTemPlateId(Integer temPlateId) {
		this.temPlateId = temPlateId;
	}
	public Integer getBussinesID() {
		return bussinesID;
	}
	public void setBussinesID(Integer bussinesID) {
		this.bussinesID = bussinesID;
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
	public Integer getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Integer isDefault) {
		this.isDefault = isDefault;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}
	private Integer temPlateId;
	private Integer bussinesID;
	private String name;
	private String description;
	private Integer isDefault;
	private String sourceType;
	private String xml;
}
