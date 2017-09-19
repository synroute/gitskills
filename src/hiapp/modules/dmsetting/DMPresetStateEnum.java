package hiapp.modules.dmsetting;

/** 
 * @author yangwentian 
 * @version 创建时间：2017年9月19日 下午8:31:35 
 * 类说明 预约状态枚举
 */
public enum DMPresetStateEnum {
	InUse("使用中"),
	FinishPreset("预约完成");
	private String	stateName;//状态名
	
	private DMPresetStateEnum(String stateName) {
		this.setStateName(stateName);	
    }
	
	public static DMPresetStateEnum getStateString(String name) {
		if (DMPresetStateEnum.InUse.getStateName().equals(name))
			return DMPresetStateEnum.InUse;
		
		if (DMPresetStateEnum.FinishPreset.getStateName().equals(name))
			return DMPresetStateEnum.FinishPreset;
		return null;
	}
	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}	
	
}
