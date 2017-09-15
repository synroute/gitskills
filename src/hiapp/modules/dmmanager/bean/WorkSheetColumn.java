package hiapp.modules.dmmanager.bean;

public class WorkSheetColumn {
	public  Integer     id;
	public	String 		nameCh;
	public	String 		name;
	public	String 		description;
	public	String 		dataType;
	public	Integer		length;
	public	Integer	isIdentitySquence;
	public	String 		dicName;
	public	Integer		dicLevel;
	public	Integer	isSysColumn;
	public	String 		workSheetColumnCh;
	public	String 		tableFieldName;
	public String workSheetId;
	public String getWorkSheetId() {
		return workSheetId;
	}
	public void setWorkSheetId(String workSheetId) {
		this.workSheetId = workSheetId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNameCh() {
		return nameCh;
	}
	public void setNameCh(String nameCh) {
		this.nameCh = nameCh;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public Integer getIsIdentitySquence() {
		return isIdentitySquence;
	}
	public void setIsIdentitySquence(Integer isIdentitySquence) {
		this.isIdentitySquence = isIdentitySquence;
	}
	public Integer getIsSysColumn() {
		return isSysColumn;
	}
	public void setIsSysColumn(Integer isSysColumn) {
		this.isSysColumn = isSysColumn;
	}
	public String getDicName() {
		return dicName;
	}
	public void setDicName(String dicName) {
		this.dicName = dicName;
	}
	
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public Integer getDicLevel() {
		return dicLevel;
	}
	public void setDicLevel(Integer dicLevel) {
		this.dicLevel = dicLevel;
	}
	public String getWorkSheetColumnCh() {
		return workSheetColumnCh;
	}
	public void setWorkSheetColumnCh(String workSheetColumnCh) {
		this.workSheetColumnCh = workSheetColumnCh;
	}
	public String getTableFieldName() {
		return tableFieldName;
	}
	public void setTableFieldName(String tableFieldName) {
		this.tableFieldName = tableFieldName;
	}
}
