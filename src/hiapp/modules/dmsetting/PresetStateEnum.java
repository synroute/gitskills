package hiapp.modules.dmsetting;
/** 
 * @author yangwentian 
 * @version 创建时间：2017年9月19日 下午8:31:35 
 * 类说明 预约状态枚举
 */
public enum PresetStateEnum {
	InUse(1,"使用中"),
	FinishPreset(2,"预约完成");
	private PresetStateEnum(int id,String stateName) {
		this.setId(id);
		this.setStateName(stateName);	
    }
	
	private int	id;//id
	private String	stateName;//状态名
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}	
}
