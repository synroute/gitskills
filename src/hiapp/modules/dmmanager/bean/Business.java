package hiapp.modules.dmmanager.bean;

public class Business {
	private Integer id;
	private String name;
	private String description;
	private String ownergroupId;
	private Integer outboundmddeId;
	private String configJson;

	public String getOwnergroupId() {
		return ownergroupId;
	}
	public void setOwnergroupId(String ownergroupId) {
		this.ownergroupId = ownergroupId;
	}
	public Integer getOutboundmddeId() {
		return outboundmddeId;
	}
	public void setOutboundmddeId(Integer outboundmddeId) {
		this.outboundmddeId = outboundmddeId;
	}
	public String getConfigJson() {
		return configJson;
	}
	public void setConfigJson(String configJson) {
		this.configJson = configJson;
	}

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
}
