package hiapp.modules.dmagent;

import java.util.List;
import java.util.Map;

public class QueryRequest {
	private String bizId;
	private List<Map<String,String>> queryCondition;
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	public List<Map<String, String>> getQueryCondition() {
		return queryCondition;
	}
	public void setQueryCondition(List<Map<String, String>> queryCondition) {
		this.queryCondition = queryCondition;
	}
	@Override
	public String toString() {
		return "QueryRequest [bizId=" + bizId + ", queryCondition="
				+ queryCondition + "]";
	}
	
	
}
