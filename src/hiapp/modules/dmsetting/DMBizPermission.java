package hiapp.modules.dmsetting;
/** 
 * @author yangwentian 
 * @version 创建时间：2017年9月8日 下午2:16:57 
 * 类说明  权限业务数据池信息
 */
public class DMBizPermission {
	private int bizId;			//业务ID
	private String bizName;		//业务名称
	private int permId;			//权限ID
	private String permName;	//权限名称
	private int dataPoolId;		//数据池ID
	private String dataPoolName;//数据池名称
	private String itemName;	//管理项名称
	public int getBizId() {
		return bizId;
	}
	public void setBizId(int bizId) {
		this.bizId = bizId;
	}
	public int getPermId() {
		return permId;
	}
	public void setPermId(int permId) {
		this.permId = permId;
	}
	public int getDataPoolId() {
		return dataPoolId;
	}
	public void setDataPoolId(int dataPoolId) {
		this.dataPoolId = dataPoolId;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getPermName() {
		return permName;
	}
	public void setPermName(String permName) {
		this.permName = permName;
	}
	public String getBizName() {
		return bizName;
	}
	public void setBizName(String bizName) {
		this.bizName = bizName;
	}
	public String getDataPoolName() {
		return dataPoolName;
	}
	public void setDataPoolName(String dataPoolName) {
		this.dataPoolName = dataPoolName;
	}
	
}
