package hiapp.modules.dmmanager;

import java.util.Date;

/** 
 * @author liuzhenda
 * @version 创建时间：2017年9月8日 下午2:40:42 
 *  类说明：数据池记录表
 */
public class DataPoolRecord {
	private Integer id; //ID
	private String sourceId;          //分配批次号
	private String importId;        //导入批次号
	private String customerId;          //客户号
	private Integer dataPoolIdLast; //上次所在数据池数据池
	private Integer dataPoolIdCur;  //当前所在数据池
	private AreaTypeEnum areaLast;  //上次所在数据池分区
	private AreaTypeEnum areaCur;    //当前所在数据池分区
	private Integer isRecover;      //是否被回收
	private String modifyUserId;    //修改人工号
	private Date modifyTime;        //修改时间

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

	public Integer getDataPoolIdLast() {
		return dataPoolIdLast;
	}

	public void setDataPoolIdLast(Integer dataPoolIdLast) {
		this.dataPoolIdLast = dataPoolIdLast;
	}

	public Integer getDataPoolIdCur() {
		return dataPoolIdCur;
	}

	public void setDataPoolIdCur(Integer dataPoolIdCur) {
		this.dataPoolIdCur = dataPoolIdCur;
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

	public Integer getIsRecover() {
		return isRecover;
	}

	public void setIsRecover(Integer isRecover) {
		this.isRecover = isRecover;
	}

	public String getModifyUserId() {
		return modifyUserId;
	}

	public void setModifyUserId(String modifyUserId) {
		this.modifyUserId = modifyUserId;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

}
