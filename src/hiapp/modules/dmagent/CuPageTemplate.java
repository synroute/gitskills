package hiapp.modules.dmagent;

import java.sql.Blob;

/**
 * @author PengXiaoHong
 * 
 * 类说明 客户页面配置模板
 */
public class CuPageTemplate {
	private long id;//ID
	private long bizId;//业务ID
	private DeployPage deployPage;//配置页面
	private DeployType deployType;//配置类型
	private String deployTemplate;//配置模板数据
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
	public DeployPage getDeployPage() {
		return deployPage;
	}
	public void setDeployPage(DeployPage deployPage) {
		this.deployPage = deployPage;
	}
	public DeployType getDeployType() {
		return deployType;
	}
	public void setDeployType(DeployType deployType) {
		this.deployType = deployType;
	}
	public String getDeployTemplate() {
		return deployTemplate;
	}
	public void setDeployTemplate(String deployTemplate) {
		this.deployTemplate = deployTemplate;
	}
	
	
	
	
}
