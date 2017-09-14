package hiapp.modules.dmagent;


/**
 * @author PengXiaoHong
 * 
 * 类说明 客户页面配置模板
 */
public class CuPageTemplate {
	private long id;//ID
	private long bizId;//业务ID
	private ConfigPageEnume configPage;//配置页面
	private ConfigTypeEnume configType;//配置类型
	private String configTemplate;//配置模板
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getBizId() {
		return bizId;
	}
	public void setBizId(long bizId) {
		this.bizId = bizId;
	}
	public ConfigPageEnume getConfigPage() {
		return configPage;
	}
	public void setConfigPage(ConfigPageEnume configPage) {
		this.configPage = configPage;
	}
	public ConfigTypeEnume getConfigType() {
		return configType;
	}
	public void setConfigType(ConfigTypeEnume configType) {
		this.configType = configType;
	}
	public String getConfigTemplate() {
		return configTemplate;
	}
	public void setConfigTemplate(String configTemplate) {
		this.configTemplate = configTemplate;
	}
	
}
