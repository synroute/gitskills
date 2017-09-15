package hiapp.modules.dmmanager;

import java.util.Date;

/** 
 * @author liuzhenda
 * @version 创建时间：2017年9月8日 下午2:22:42 
 *  类说明：导入批次信息表
 */
public class ImportBatchMassage {
	
	private Integer id;            //ID
	private String importId;       //导入批次id
	private Integer bizId;         //业务编号
	private Date importTime;       //导入时间
	private String userId;         //导入人
	private String importName;     //导入批次名称
	private String description;    //导入批次描述
	private String dataType;       //数据来源
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getiId() {
		return importId;
	}
	public void setiId(String importId) {
		this.importId = importId;
	}
	public Integer getBusinessId() {
		return bizId;
	}
	public void setBusinessId(Integer businessId) {
		this.bizId = businessId;
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
		return importName;
	}
	public void setName(String importName) {
		this.importName = importName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImportType() {
		return dataType;
	}
	public void setImportType(String importType) {
		this.dataType = importType;
	}
	
}
