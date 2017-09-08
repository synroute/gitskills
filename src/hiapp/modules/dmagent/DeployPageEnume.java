package hiapp.modules.dmagent;

public enum DeployPageEnume {
	MYCUSTOMERS("我的客户"),CONTACTPLAN("联系计划"),ALLCUSTOMERS("全部客户");
	private String msg;
	DeployPageEnume(String msg){
		this.msg = msg;
	}
	public String getMsg(){
		return this.msg;
	}
}
