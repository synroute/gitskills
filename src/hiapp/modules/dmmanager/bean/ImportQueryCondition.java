package hiapp.modules.dmmanager.bean;

import java.util.List;
import java.util.Map;

public class ImportQueryCondition {
	private List<Map<String,Object>> importData;

	public List<Map<String, Object>> getImportData() {
		return importData;
	}

	public void setImportData(List<Map<String, Object>> importData) {
		this.importData = importData;
	}
}
