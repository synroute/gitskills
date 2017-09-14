package hiapp.modules.dmagent;

import java.util.List;
import java.util.Map;

public class QueryTemplate {
	private String bizId;
	private String configPage;
	private String configType;
	private List<Map<String,String>> configTemplate;
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	public String getConfigPage() {
		return configPage;
	}
	public void setConfigPage(String configPage) {
		this.configPage = configPage;
	}
	public String getConfigType() {
		return configType;
	}
	public void setConfigType(String configType) {
		this.configType = configType;
	}
	public List<Map<String, String>> getConfigTemplate() {
		return configTemplate;
	}
	public void setConfigTemplate(List<Map<String, String>> configTemplate) {
		this.configTemplate = configTemplate;
	}
	@Override
	public String toString() {
		return "QueryTemplate [bizId=" + bizId + ", configPage=" + configPage
				+ ", configType=" + configType + ", configTemplate="
				+ configTemplate + "]";
	}
}
