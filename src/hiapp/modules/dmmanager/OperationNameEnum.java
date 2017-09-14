package hiapp.modules.dmmanager;

public enum OperationNameEnum {
      
	SystemSynchro("系统同步"),
	ExcelImport("Excel导入"),
	MyImport("自我录入"),
	Distribution("分配 "),
	Sharing("共享"),
	Extract("抽取"),
	Recycle("回收 "),
	Rollback("回退"); 
	private String name;
	OperationNameEnum(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
}
