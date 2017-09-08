package hiapp.modules.dmagent.data;

import hiapp.modules.dmagent.DeployPageEnume;
import hiapp.modules.dmagent.DeployTypeEnume;

import java.sql.Blob;

/**
 * 客户页面配置表HASYS_DM_CUPAGETEMPLATE
 * @author PengXiaoHong
 */
public class CuPageTemplate {
	private long id;//ID
	private long bizId;//业务ID
	private DeployPageEnume deployPageEnume;//配置页面
	private DeployTypeEnume deployTypeEnume;//配置类型
	private Blob Templatedata;//配置模板
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
	public DeployPageEnume getDeployPageEnume() {
		return deployPageEnume;
	}
	public void setDeployPageEnume(DeployPageEnume deployPageEnume) {
		this.deployPageEnume = deployPageEnume;
	}
	public DeployTypeEnume getDeployTypeEnume() {
		return deployTypeEnume;
	}
	public void setDeployTypeEnume(DeployTypeEnume deployTypeEnume) {
		this.deployTypeEnume = deployTypeEnume;
	}
	public Blob getTemplatedata() {
		return Templatedata;
	}
	public void setTemplatedata(Blob templatedata) {
		Templatedata = templatedata;
	}
	
}
