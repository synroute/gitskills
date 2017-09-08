package hiapp.modules.dmagent;

public enum DeployPage {
	MYCUSTOMERS("我的客户"),CONTACTPLAN("联系计划"),ALLCUSTOMERS("全部客户");
	private String name;
	DeployPage(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
}
