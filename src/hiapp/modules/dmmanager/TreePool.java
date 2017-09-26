package hiapp.modules.dmmanager;

public class TreePool {

	private Integer id;
	private String DataPoolName;
	private Integer Pid;
	private Integer groupId;
	private String groupName;
	private String userName;
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDataPoolName() {
		return DataPoolName;
	}
	public void setDataPoolName(String dataPoolName) {
		DataPoolName = dataPoolName;
	}
	public Integer getPid() {
		return Pid;
	}
	public void setPid(Integer pid) {
		Pid = pid;
	}

}
