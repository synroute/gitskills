package hiapp.modules.dmsetting;

/** 
 * @author liuhao
 * @version 创建时间：2017年9月8日 下午4:19:28 
 * 类说明  客户信息导入表信息
 */
public class DMCustomerInfomation {
	//自增id
	private int	id;
	//导入批次id
	private String	importId;
	//客户id
	private String	customerId;
	//是否为最后一次修改
	private String	modifyLast;
	//修改id
	private String modifyId;
	//修改人工号
	private String modifyUserid;
	//修改时间
	private String modifyTime;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getModifyLast() {
		return modifyLast;
	}
	public void setModifyLast(String modifyLast) {
		this.modifyLast = modifyLast;
	}
	public String getModifyId() {
		return modifyId;
	}
	public void setModifyId(String modifyId) {
		this.modifyId = modifyId;
	}
	public String getModifyUserid() {
		return modifyUserid;
	}
	public void setModifyUserid(String modifyUserid) {
		this.modifyUserid = modifyUserid;
	}
	public String getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	public String getImportId() {
		return importId;
	}
	public void setImportId(String importId) {
		this.importId = importId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
}
