package hiapp.modules.dmagent;

import java.util.List;

public class QueryRequest {
    private String bizId;
    private List<QueryCondition> queryCondition;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public List<QueryCondition> getQueryCondition() {
        return queryCondition;
    }

    public void setQueryCondition(List<QueryCondition> queryCondition) {
        this.queryCondition = queryCondition;
    }
}
