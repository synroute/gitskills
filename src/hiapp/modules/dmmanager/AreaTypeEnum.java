package hiapp.modules.dmmanager;

public enum AreaTypeEnum {
    DA(1),SA(2),NO(3);
	
	private Integer id;
	public Integer getId() {
		return id;
	}
	AreaTypeEnum(Integer id){
		this.id = id;
	}

	public static  AreaTypeEnum getFromInt(int areaType) {
		if (1 == areaType)
			return AreaTypeEnum.DA;
		else if (2 == areaType)
			return AreaTypeEnum.SA;
		else if (3 == areaType)
			return AreaTypeEnum.NO;

		return AreaTypeEnum.NO;
	}
}
