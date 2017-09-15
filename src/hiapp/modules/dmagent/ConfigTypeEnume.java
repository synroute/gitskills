package hiapp.modules.dmagent;

public enum ConfigTypeEnume {
	ADVANCEDQUERY("高级筛选"),BASEQUERY("基础筛选"),CUSTOMERLIST("客户列表");
	private String name;
	ConfigTypeEnume(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
}
