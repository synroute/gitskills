package hiapp.modules.dmagent;

public enum DeployPage {
	MYCUSTOMERS("我的客户"),CONTACTPLAN("联系计划"),ALLCUSTOMERS("全部客户");
	private String msg;
	DeployPage(String msg){
		this.msg = msg;
	}
	public String getMsg(){
		return this.msg;
	}
}
