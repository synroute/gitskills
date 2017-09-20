package hiapp.modules.dmmanager;

public class TreePool {

	private Integer id;
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
	private String DataPoolName;
	private Integer Pid;
}
