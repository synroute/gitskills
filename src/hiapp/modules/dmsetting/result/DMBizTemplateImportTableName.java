package hiapp.modules.dmsetting.result;
/** 
 * @author 刘浩 
 * @version 创建时间：2017年9月12日 上午11:04:09 
 * 类说明  数据库所有表名称信息
 */

public class DMBizTemplateImportTableName {
	private String tableName;//表名
	private String comments;//表描述
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
}
