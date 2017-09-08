package hiapp.modules.dmmanager;

/** 
 * @author liuzhenda
 * @version 创建时间：2017年9月8日 下午2:10:42 
 *  类说明：座席池所属共享批次信息表
 */
public class UserPoolShareBatchMassage {
	
	private Integer id;          //ID
	private Integer businessId;  //业务号
	private String shareId;      //共享批次号
	private String dataPoolName; //数据池名称
	private Integer dataPoolId;  //数据池编号
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getBusinessId() {
		return businessId;
	}
	public void setBusinessId(Integer businessId) {
		this.businessId = businessId;
	}
	public String getShareId() {
		return shareId;
	}
	public void setShareId(String shareId) {
		this.shareId = shareId;
	}
	public String getDataPoolName() {
		return dataPoolName;
	}
	public void setDataPoolName(String dataPoolName) {
		this.dataPoolName = dataPoolName;
	}
	public Integer getDataPoolId() {
		return dataPoolId;
	}
	public void setDataPoolId(Integer dataPoolId) {
		this.dataPoolId = dataPoolId;
	}
	
}
