package hiapp.modules.dmmanager;

import java.util.Date;

/** 
 * @author liuzhenda
 * @version 创建时间：2017年9月8日 下午2:30:42 
 *  类说明：数据池记录操作表
 */
public class DataPoolRecordOperation {
	
	private Integer id;             //ID
	private String sourceId;          //分配批次号
	private String importId;        //导入批次号
	private String customerId;          //客户号
	private OperationNameEnum operationName;   //操作类型OperationName
	private Integer dataPoolIDLast; //上次所在数据池数据池
	private Integer dataPoolIDCur;  //当前所在数据池
	private AreaTypeEnum areaLast;       //上次所在数据池分区
	private AreaTypeEnum areaCur;   //当前所在数据池分区
	private IsRecoverEnum iSRecover;      //是否被回收
	private String modifyUserID;    //修改人工号
	private Date ModifyTime;        //修改时间

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
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

	public OperationNameEnum getOperationName() {
		return operationName;
	}

	public void setOperationName(OperationNameEnum operationName) {
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

	public AreaTypeEnum getAreaLast() {
		return areaLast;
	}

	public void setAreaLast(AreaTypeEnum areaLast) {
		this.areaLast = areaLast;
	}

	public AreaTypeEnum getAreaCur() {
		return areaCur;
	}

	public void setAreaCur(AreaTypeEnum areaCur) {
		this.areaCur = areaCur;
	}

	public IsRecoverEnum getiSRecover() {
		return iSRecover;
	}

	public void setiSRecover(IsRecoverEnum iSRecover) {
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
