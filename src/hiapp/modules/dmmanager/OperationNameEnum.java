package hiapp.modules.dmmanager;

import hiapp.modules.dm.bo.ShareBatchStateEnum;

public enum OperationNameEnum {
      
	SystemSynchro("DB"),
	ExcelImport("Excel"),
	MyImport("自我录入"),
	Distribution("分配 "),
	Sharing("共享"),
	APPERND("追加共享"),
	Extract("抽取"),
	Recycle("回收 "),
	Rollback("回退"),
	CANCELLED("取消共享");

	private String name;
	OperationNameEnum(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}

	static public OperationNameEnum getFromString(String dbValue) {
		if (OperationNameEnum.SystemSynchro.getName().equals(dbValue))
			return OperationNameEnum.SystemSynchro;

		if (OperationNameEnum.ExcelImport.getName().equals(dbValue))
			return OperationNameEnum.ExcelImport;

		if (OperationNameEnum.MyImport.getName().equals(dbValue))
			return OperationNameEnum.MyImport;

		if (OperationNameEnum.Distribution.getName().equals(dbValue))
			return OperationNameEnum.Distribution;

		if (OperationNameEnum.Sharing.getName().equals(dbValue))
			return OperationNameEnum.Sharing;

		if (OperationNameEnum.APPERND.getName().equals(dbValue))
			return OperationNameEnum.APPERND;

		if (OperationNameEnum.Extract.getName().equals(dbValue))
			return OperationNameEnum.Extract;

		if (OperationNameEnum.Recycle.getName().equals(dbValue))
			return OperationNameEnum.Recycle;

		if (OperationNameEnum.Rollback.getName().equals(dbValue))
			return OperationNameEnum.Rollback;

		if (OperationNameEnum.CANCELLED.getName().equals(dbValue))
			return OperationNameEnum.CANCELLED;

		return null;
	}

}
