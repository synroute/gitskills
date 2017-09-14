package hiapp.modules.dmmanager;

public enum AreaLastEnum {
	
	DA(1),SA(2),NO(3);
	
	private Integer id;
	public Integer getId() {
		return id;
	}
	AreaLastEnum(Integer id){
		this.id = id;
	}
}
