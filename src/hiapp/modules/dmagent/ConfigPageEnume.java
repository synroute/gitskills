package hiapp.modules.dmagent;

public enum ConfigPageEnume {
	MYCUSTOMERS("我的客户"),CONTACTPLAN("待处理"),ALLCUSTOMERS("全部客户");
	private String name;
	ConfigPageEnume(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
}
