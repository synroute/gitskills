package hiapp.modules.dmsetting;
/** 
 * @author liuhao 
 * @version 创建时间：2017年9月8日 下午02:15:42 
 * 类说明  用于存储数据池信息
 */
public class DMDataPool {
	//数据池编号
	private int id;
	//数据池名称
	private String dataPoolName;
	//数据池类型
	private String dataPoolType;
	//数据池描述
	private String dataPoolDes;
	//父节点
	private int pid;
	//分区类型
	private int areaType;
	//数据池上限
	private int poolTopLimit;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDataPoolName() {
		return dataPoolName;
	}
	public void setDataPoolName(String dataPoolName) {
		this.dataPoolName = dataPoolName;
	}
	public String getDataPoolType() {
		return dataPoolType;
	}
	public void setDataPoolType(String dataPoolType) {
		this.dataPoolType = dataPoolType;
	}
	public String getDataPoolDes() {
		return dataPoolDes;
	}
	public void setDataPoolDes(String dataPoolDes) {
		this.dataPoolDes = dataPoolDes;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public int getAreaType() {
		return areaType;
	}
	public void setAreaType(int areaType) {
		this.areaType = areaType;
	}
	public int getPoolTopLimit() {
		return poolTopLimit;
	}
	public void setModeId(int poolTopLimit) {
		this.poolTopLimit = poolTopLimit;
	}
	
}
