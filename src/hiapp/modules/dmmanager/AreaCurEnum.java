package hiapp.modules.dmmanager;

public enum AreaCurEnum {
    DA(1),SA(2),NO(3);
	
	private Integer id;
	public Integer getId() {
		return id;
	}
	AreaCurEnum(Integer id){
		this.id = id;
	}
}
