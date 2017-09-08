package hiapp.modules.dmsetting;

import java.sql.Date;

/** 
 * @author yangwentian
 * @version 创建时间：2017年9月8日 下午4:19:28 
 * 类说明 预约项目信息
 */
public class DMBizPreset {
	private int id;				//ID
	private String sourceId;	//来源编号，指分配编号或共享编号
	private String Iid;			//导入批次ID
	private String Cid;			//客户号
	private Date presetTime;	//预约时间
	private String state;		//预约状态
	private String comment;		//预约备注
	private int modifyId;		//修改ID
	private int modifyLast;		//是否为最后一次修改，0：否。1：是
	private int modifyUserId;	//修改用户ID
	private Date modifyTime;	//修改时间
	private String modifyDesc;	//修改描述
	private String PhoneType;	//号码类型
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getIid() {
		return Iid;
	}
	public void setIid(String iid) {
		Iid = iid;
	}
	public String getCid() {
		return Cid;
	}
	public void setCid(String cid) {
		Cid = cid;
	}
	public Date getPresetTime() {
		return presetTime;
	}
	public void setPresetTime(Date presetTime) {
		this.presetTime = presetTime;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public int getModifyId() {
		return modifyId;
	}
	public void setModifyId(int modifyId) {
		this.modifyId = modifyId;
	}
	public int getModifyLast() {
		return modifyLast;
	}
	public void setModifyLast(int modifyLast) {
		this.modifyLast = modifyLast;
	}
	public int getModifyUserId() {
		return modifyUserId;
	}
	public void setModifyUserId(int modifyUserId) {
		this.modifyUserId = modifyUserId;
	}
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	public String getModifyDesc() {
		return modifyDesc;
	}
	public void setModifyDesc(String modifyDesc) {
		this.modifyDesc = modifyDesc;
	}
	public String getPhoneType() {
		return PhoneType;
	}
	public void setPhoneType(String phoneType) {
		PhoneType = phoneType;
	}
}
