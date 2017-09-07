package hiapp.modules.dmsetting;

public class ImportMapColumn {
	private String name;
	private String nameCh;
	private String description;
	private String excelRowNumber;
	private String excelColumnName;
	private int dbFieldIndex;
	private String ColIndex;
	private String CellAddr;
	private String RowIndex;
	private String Worksheetid;
	private String WorksheetName;
	private String WorksheetNameCh;
	private String xmlWorksheetid;
	private String xmlWorksheetName;
	private String xmlWorkSheetColName;
	public ImportMapColumn(){
		dbFieldIndex=-1;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNameCh() {
		return nameCh;
	}
	public void setNameCh(String nameCh) {
		this.nameCh = nameCh;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getExcelRowNumber() {
		return excelRowNumber;
	}
	public void setExcelRowNumber(String excelRowNumber) {
		this.excelRowNumber = excelRowNumber;
	}
	public String getExcelColumnName() {
		if(excelColumnName==null) return "";
		return excelColumnName;
	}
	public void setExcelColumnName(String excelColumnName) {
		this.excelColumnName = excelColumnName;
	}
	public int getDbFieldIndex() {
		return dbFieldIndex;
	}
	public void setDbFieldIndex(int dbFieldIndex) {
		this.dbFieldIndex = dbFieldIndex;
	}
	
	public String getColIndex() {
		return ColIndex;
	}
	public void setColIndex(String ColIndex) {
		this.ColIndex = ColIndex;
	}
	
	public String getCellAddr() {
		return CellAddr;
	}
	public void setCellAddr(String CellAddr) {
		this.CellAddr = CellAddr;
	}
	public String getWorksheetid() {
		return Worksheetid;
	}
	
	public void setWorksheetid(String Worksheetid) {
		this.Worksheetid = Worksheetid;
	}
	public void setRowIndex(String RowIndex) {
		this.RowIndex = RowIndex;
	}
	
	public String getRowIndex() {
		return RowIndex;
	}
	public String getWorksheetName() {
		return WorksheetName;
	}
	public void setWorksheetName(String WorksheetName) {
		this.WorksheetName = WorksheetName;
	}
	
	public String getWorksheetNameCh() {
		return WorksheetNameCh;
	}
	public void setWorksheetNameCh(String WorksheetNameCh) {
		this.WorksheetNameCh = WorksheetNameCh;
	}
	
	public String getxmlWorksheetid() {
		return xmlWorksheetid;
	}
	public void setxmlWorksheetid(String xmlWorksheetid) {
		this.xmlWorksheetid = xmlWorksheetid;
	}
	public String getxmlWorksheetName() {
		return xmlWorksheetName;
	}
	public void setxmlWorksheetName(String xmlWorksheetName) {
		this.xmlWorksheetName = xmlWorksheetName;
	}
	
	public String getxmlWorkSheetColName() {
		return xmlWorkSheetColName;
	}
	public void setxmlWorkSheetColName(String xmlWorkSheetColName) {
		this.xmlWorkSheetColName = xmlWorkSheetColName;
	}
}
