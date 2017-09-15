package hiapp.modules.dmmanager;

public enum IsRecoverEnum {
YES(1),NO(0);
	
	private Integer id;
	public Integer getId() {
		return id;
	}
	IsRecoverEnum(Integer id){
		this.id = id;
	}
}
