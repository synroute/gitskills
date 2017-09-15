package hiapp.modules.dmsetting;

/** 
 * @author liuhao
 * @version 创建时间：2017年9月8日 下午4:19:28 
 * 类说明  数据池信息
 */
public class DMDataPool {
	//数据池编号
	private int poolId;
	//业务编号
	private int bizId;
	//数据池名称
	private String dataPoolName;
	//数据池类型
	private String dataPoolType;
	//数据池描述
	private String dataPoolDesc;
	//父节点
	private int pid;
	//分区烈性
	private int areaType;
	//数据池上限
	private int poolTopLimit;
	
	
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
	public String getDataPoolDesc() {
		return dataPoolDesc;
	}
	public void setDataPoolDesc(String dataPoolDesc) {
		this.dataPoolDesc = dataPoolDesc;
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
	public void setPoolTopLimit(int poolTopLimit) {
		this.poolTopLimit = poolTopLimit;
	}
	public int getBizId() {
		return bizId;
	}
	public void setBizId(int bizId) {
		this.bizId = bizId;
	}
	public int getPoolId() {
		return poolId;
	}
	public void setPoolId(int poolId) {
		this.poolId = poolId;
	}
	
}
