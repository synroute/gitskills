package hiapp.modules.dmmanager;

import java.util.Date;

/** 
 * @author liuzhenda
 * @version 创建时间：2017年9月8日 下午2:22:42 
 *  类说明：导入批次信息表
 */
public class ImportBatchMassage {
	
	private Integer id;            //ID
	private String iId;            //导入批次id
	private Integer businessId;    //业务编号
	private Date importTime;       //导入时间
	private String userId;         //导入人
	private String name;           //导入批次名称
	private String description;    //导入批次描述
	private String importType;     //数据来源
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getiId() {
		return iId;
	}
	public void setiId(String iId) {
		this.iId = iId;
	}
	public Integer getBusinessId() {
		return businessId;
	}
	public void setBusinessId(Integer businessId) {
		this.businessId = businessId;
	}
	public Date getImportTime() {
		return importTime;
	}
	public void setImportTime(Date importTime) {
		this.importTime = importTime;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImportType() {
		return importType;
	}
	public void setImportType(String importType) {
		this.importType = importType;
	}
	
}
