package hiapp.modules.dmsetting;
/** 
 * @author liuhao
 * @version 创建时间：2017年9月8日 下午3:39:09 
 * 类说明  业务号码类型信息
 */
public class DMBizPhoneType {
		//业务编号
		private String bizId;
		//号码类型名称
		private String	name;
		//号码类型中文名称
		private String	nameCh;
		//描述
		private String	description;
		//拨打顺序
		private int	dialOrder;
		//对应导入字段
		private String customerColumnMap;
		private int dialType;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getNameCh() {
			return nameCh;
		}
		public void setNameCh(String nameCh) {
			this.nameCh = nameCh;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public int getDialOrder() {
			return dialOrder;
		}
		public void setDialOrder(int dialOrder) {
			this.dialOrder = dialOrder;
		}
		public String getCustomerColumnMap() {
			return customerColumnMap;
		}
		public void setCustomerColumnMap(String customerColumnMap) {
			this.customerColumnMap = customerColumnMap;
		}
		public String getBizId() {
			return bizId;
		}
		public void setBizId(String bizId) {
			this.bizId = bizId;
		}
		public int getDialType() {
			return dialType;
		}
		public void setDialType(int dialType) {
			this.dialType = dialType;
		}
}
