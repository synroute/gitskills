package hiapp.modules.dmmanager;

import hiapp.utils.serviceresult.TreeBranch;

public class UserPoolTreeBranch extends TreeBranch{
private String id;
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getDataPoolName() {
	return dataPoolName;
}
public void setDataPoolName(String dataPoolName) {
	this.dataPoolName = dataPoolName;
}
public Integer getPid() {
	return pid;
}
public void setPid(Integer pid) {
	this.pid = pid;
}
private String dataPoolName;
private Integer pid;
}
