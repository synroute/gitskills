package hiapp.modules.dmagent;

import java.util.List;
import java.util.Map;

public class QueryRequest {
	private String bizId;
	private int page;
	private int rows;
	private String IID;
	private String CID;
	private String SourceId;
	private List<Map<String, String>> queryCondition;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public boolean hasQueryNext(){
		return IID!=null&&CID!=null&&SourceId!=null;
	}

	public int getStart() {
		int start = (page - 1) * rows + 1;
		if(hasQueryNext()){
			start = 1;
		}
		return start;
	}

	public int getEnd() {
		int end = page * rows;
		if(hasQueryNext()){
			end = rows - 1;
		}
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
		return page;
	}

	public void setPageNum(int pageNum) {
		this.page = pageNum;
	}

	public int getPageSize() {
		return rows;
	}

	public void setPageSize(int pageSize) {
		this.rows = pageSize;
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
