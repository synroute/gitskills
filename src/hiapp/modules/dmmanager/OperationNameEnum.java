package hiapp.modules.dmmanager;

public enum OperationNameEnum {
      
	SystemSynchro("DB"),
	ExcelImport("Excel"),
	MyImport("自我录入"),
	Distribution("分配 "),
	Sharing("共享"),
	APPERND("追加共享"),
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
