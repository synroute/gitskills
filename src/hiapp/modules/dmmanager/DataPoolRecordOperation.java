package hiapp.modules.dmmanager;

import java.util.Date;

/** 
 * @author liuzhenda
 * @version 创建时间：2017年9月8日 下午2:30:42 
 *  类说明：数据池记录操作表
 */
public class DataPoolRecordOperation {
	
	private Integer id;             //ID
	private String distId;          //分配批次号
	private String importId;        //导入批次号
	private String custId;          //客户号
	private String operationName;   //操作类型
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
		return distId;
	}
	public void setdId(String dId) {
		this.distId = dId;
	}
	public String getiId() {
		return importId;
	}
	public void setiId(String importId) {
		this.importId = importId;
	}
	public String getcId() {
		return custId;
	}
	public void setcId(String cId) {
		this.custId = cId;
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
