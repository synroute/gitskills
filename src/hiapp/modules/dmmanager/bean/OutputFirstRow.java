package hiapp.modules.dmmanager.bean;

public class OutputFirstRow {
	private String title;
	private String field;
	private String excelHeader;
	private String dataType;
	private String workSheetId;
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getWorkSheetId() {
		return workSheetId;
	}
	public void setWorkSheetId(String workSheetId) {
		this.workSheetId = workSheetId;
	}
	public String getExcelHeader() {
		return excelHeader;
	}
	public void setExcelHeader(String excelHeader) {
		this.excelHeader = excelHeader;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
}
