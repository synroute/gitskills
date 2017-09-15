package hiapp.modules.dmmanager;

import javax.xml.crypto.Data;

public class ImportDataMessage {

	private Integer id;
	private String iid;          //导入批次id
	private Integer businessId;  //业务编号
	private Data ImportTime;     //导入时间
	private String userId;       //导入人
	private String name;         //导入批次名称
	private String description;  //导入批次描述
	private String importType;   //数据来源
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIid() {
		return iid;
	}
	public void setIid(String iid) {
		this.iid = iid;
	}
	public Integer getBusinessId() {
		return businessId;
	}
	public void setBusinessId(Integer businessId) {
		this.businessId = businessId;
	}
	public Data getImportTime() {
		return ImportTime;
	}
	public void setImportTime(Data importTime) {
		ImportTime = importTime;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
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
	public String getImportType() {
		return importType;
	}
	public void setImportType(String importType) {
		this.importType = importType;
	}
	
}
