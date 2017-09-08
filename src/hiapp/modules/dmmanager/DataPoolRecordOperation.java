package hiapp.modules.dmmanager;

import java.util.Date;

/** 
 * @author liuzhenda
 * @version 创建时间：2017年9月8日 下午2:30:42 
 *  类说明：数据池记录操作表
 */
public class DataPoolRecordOperation {
	
	private Integer id;             //ID
	private String dId;             //分配批次号
	private String iId;             //导入批次号
	private String cId;             //客户号	CID
	private String operationName;   //本次操作
	private Integer dataPoolIDLast; //上次所在数据池数据池
	private Integer dataPoolIDCur;  //当前所在数据池
	private Integer areaLast;       //上次所在数据池分区
	private Integer areaCur;        //当前所在数据池分区
	public Integer getAreaCur() {
		return areaCur;
	}
	public void setAreaCur(Integer areaCur) {
		this.areaCur = areaCur;
	}
	private Integer iSRecover;      //是否被回收
	private String modifyUserID;    //修改人工号
	private Date ModifyTime;        //修改时间
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getdId() {
		return dId;
	}
	public void setdId(String dId) {
		this.dId = dId;
	}
	public String getiId() {
		return iId;
	}
	public void setiId(String iId) {
		this.iId = iId;
	}
	public String getcId() {
		return cId;
	}
	public void setcId(String cId) {
		this.cId = cId;
	}
	public String getOperationName() {
		return operationName;
	}
	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}
	public Integer getDataPoolIDLast() {
		return dataPoolIDLast;
	}
	public void setDataPoolIDLast(Integer dataPoolIDLast) {
		this.dataPoolIDLast = dataPoolIDLast;
	}
	public Integer getDataPoolIDCur() {
		return dataPoolIDCur;
	}
	public void setDataPoolIDCur(Integer dataPoolIDCur) {
		this.dataPoolIDCur = dataPoolIDCur;
	}
	public Integer getAreaLast() {
		return areaLast;
	}
	public void setAreaLast(Integer areaLast) {
		this.areaLast = areaLast;
	}
	public Integer getiSRecover() {
		return iSRecover;
	}
	public void setiSRecover(Integer iSRecover) {
		this.iSRecover = iSRecover;
	}
	public String getModifyUserID() {
		return modifyUserID;
	}
	public void setModifyUserID(String modifyUserID) {
		this.modifyUserID = modifyUserID;
	}
	public Date getModifyTime() {
		return ModifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		ModifyTime = modifyTime;
	}
}
