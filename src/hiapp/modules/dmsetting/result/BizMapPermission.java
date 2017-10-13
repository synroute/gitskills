package hiapp.modules.dmsetting.result;
/** 
 * @author yangwentian 
 * @version 创建时间：2017年10月12日 下午3:15:56 
 * 类说明 权限关联业务
 */
public class BizMapPermission {
	private int bizId;//业务ID
	private String bizName;//业务名称
	private int permissionId;//权限ID
	private String permissionName;//权限名称
	private String permItemName;//权限项名称
	public int getBizId() {
		return bizId;
	}
	public void setBizId(int bizId) {
		this.bizId = bizId;
	}
	public String getBizName() {
		return bizName;
	}
	public void setBizName(String bizName) {
		this.bizName = bizName;
	}
	public int getPermissionId() {
		return permissionId;
	}
	public void setPermissionId(int permissionId) {
		this.permissionId = permissionId;
	}
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	public String getPermItemName() {
		return permItemName;
	}
	public void setPermItemName(String permItemName) {
		this.permItemName = permItemName;
	}
}
