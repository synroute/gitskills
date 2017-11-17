package hiapp.modules.dmmanager;

public enum AreaTypeEnum {
    DA(0),SA(1),NO(2);
	
	private Integer id;
	public Integer getId() {
		return id;
	}
	AreaTypeEnum(Integer id){
		this.id = id;
	}

	public static  AreaTypeEnum getFromInt(int areaType) {
		if (0 == areaType)
			return AreaTypeEnum.DA;
		else if (1== areaType)
			return AreaTypeEnum.SA;
		else if (2== areaType)
			return AreaTypeEnum.NO;

		return AreaTypeEnum.NO;
	}
}
