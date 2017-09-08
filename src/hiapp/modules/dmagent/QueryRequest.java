package hiapp.modules.dmagent;

import java.util.List;

public class QueryRequest {
    private String bizId;
    private String queryType;
    private List<QueryCondition> queryCondition;
    
	public QueryRequest() {
	}
	
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	public List<QueryCondition> getQueryCondition() {
		return queryCondition;
	}
	public void setQueryCondition(List<QueryCondition> queryCondition) {
		this.queryCondition = queryCondition;
	}

    
}
