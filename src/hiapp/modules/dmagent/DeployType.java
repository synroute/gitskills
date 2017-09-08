package hiapp.modules.dmagent;

public enum DeployType {
	ADVANCEDQUERY("高级筛选"),BASEQUERY("基础筛选"),CUSTOMERLIST("客户列表");
	private String msg;
	DeployType(String msg){
		this.msg = msg;
	}
	public String getMsg(){
		return this.msg;
	}
}
