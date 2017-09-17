package hiapp.modules.dmagent;

import java.util.List;
import java.util.Map;

public class QueryRequest {
	private String bizId;
	private int pageNum = 1;
	private int pageSize = 20;
	private String IID;
	private String CID;
	private String SourceId;
	private List<Map<String, String>> queryCondition;
	
	public boolean hasQueryNext(){
		return IID!=null&&CID!=null&&SourceId!=null;
	}

	public int getStart() {
		int start = (pageNum - 1) * pageSize + 1;
		return start;
	}

	public int getEnd() {
		int end = pageNum * pageSize;
		return end;
	}

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

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getIID() {
		return IID;
	}

	public void setIID(String iID) {
		IID = iID;
	}

	public String getCID() {
		return CID;
	}

	public void setCID(String cID) {
		CID = cID;
	}

	public String getSourceId() {
		return SourceId;
	}

	public void setSourceId(String sourceId) {
		SourceId = sourceId;
	}

}
