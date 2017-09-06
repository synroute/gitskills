package hiapp.modules.dmsetting.data;

public enum DMWorkSheetType {
	WSTDM_CUSTOMERIMPORT(1,"�ͻ����빤����"),
	WSTDM_CUSTOMERTASK(2,"�ͻ���������"),
	WSTDM_RESULT(2,"�ͻ���������"),
	WSTDM_CUSTDIST(3,"�ͻ����乤����"),
	WSTDM_CUSTDIST_HIS(4,"�ͻ�������ʷ������"),
	WSTDM_PRESETTIME(5,"ԤԼ������"),
	WSTDM_PRESETTIME_HIS(6,"ԤԼ��ʷ������"),
	WSTDM_USERDEFINE(7,"�û��Զ��幤����");
	private int code;
    private String type;  
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
    private DMWorkSheetType(int code,String type) {
    	this.code=code;
    	this.type=type;
    }  
}
