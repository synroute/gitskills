package hiapp.modules.dmsetting;

import hiapp.system.worksheet.bean.WorkSheet;

public class DMWorkSheet extends WorkSheet {
	private String worksheetType;
	private String idNameCh;
	public String getWorksheetType() {
		return worksheetType;
	}
	public void setWorksheetType(String worksheetType) {
		this.worksheetType = worksheetType;
	}
	public String getIdNameCh() {
		return idNameCh;
	}
	public void setIdNameCh(String idNameCh) {
		this.idNameCh = idNameCh;
	}
}
