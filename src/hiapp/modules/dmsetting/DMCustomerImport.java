package hiapp.modules.dmsetting;
/** 
 * @author liuhao 
 * @version 创建时间：2017年9月8日 下午02:15:42 
 * 类说明  用于存储导入表信息
 */
public class DMCustomerImport {
	//自增id
	private int	id;
	//导入批次id
	private String	iid;
	//客户id
	private String	cid;
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
	public String getIid() {
		return iid;
	}
	public void setIid(String iid) {
		this.iid = iid;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
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
}
