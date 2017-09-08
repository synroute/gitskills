package hiapp.modules.dmmanager;

import java.util.Date;

/** 
 * @author liuzhenda
 * @version 创建时间：2017年9月8日 下午2:40:42 
 *  类说明：数据池记录表
 */
public class DataPoolRecord {
	private Integer id;             //ID
	private String distId;          //分配批次号
	private String importId;        //导入批次号
	private String custId;          //客户号
	private Integer dataPoolIdLast; //上次所在数据池数据池
	private Integer dataPoolIdCur;  //当前所在数据池
	private Integer areaLast;       //上次所在数据池分区
	private Integer areaCur;        //当前所在数据池分区
	private Integer isRecover;      //是否被回收
	private String modifyUserId;    //修改人工号
	private Date modifyTime;        //修改时间
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
	public void setcId(String custId) {
		this.custId = custId;
	}
	public Integer getDataPoolIDLast() {
		return dataPoolIdLast;
	}
	public void setDataPoolIDLast(Integer dataPoolIdLast) {
		this.dataPoolIdLast = dataPoolIdLast;
	}
	public Integer getDataPoolIDCur() {
		return dataPoolIdCur;
	}
	public void setDataPoolIDCur(Integer dataPoolIDCur) {
		this.dataPoolIdCur = dataPoolIDCur;
	}
	public Integer getAreaLast() {
		return areaLast;
	}
	public void setAreaLast(Integer areaLast) {
		this.areaLast = areaLast;
	}
	public Integer getAreaCur() {
		return areaCur;
	}
	public void setAreaCur(Integer areaCur) {
		this.areaCur = areaCur;
	}
	public Integer getIsRecover() {
		return isRecover;
	}
	public void setIsRecover(Integer isRecover) {
		this.isRecover = isRecover;
	}
	public String getModifyUserID() {
		return modifyUserId;
	}
	public void setModifyUserID(String modifyUserId) {
		this.modifyUserId = modifyUserId;
	}
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
}
