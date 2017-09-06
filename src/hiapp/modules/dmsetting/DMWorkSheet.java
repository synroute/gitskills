package hiapp.modules.dmsetting;

import hiapp.system.worksheet.dblayer.WorkSheet;

public class DMWorkSheet extends WorkSheet {
	private String type;
	private String idNameCh;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIdNameCh() {
		return idNameCh;
	}
	public void setIdNameCh(String idNameCh) {
		this.idNameCh = idNameCh;
	}
}
