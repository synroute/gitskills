package hiapp.modules.dmsetting;

import org.springframework.http.StreamingHttpOutputMessage;


/** 
 * @author liuhao
 * @version 创建时间：2017年9月8日 下午4:19:28 
 * 类说明 结果表信息
 */

public class DMBizResult {
		//自增id
		private int	id;
		//来源
		private String sourceId;
		//客户导入批次id
		private String	iid;
		//客户id
		private String	cid;
		//是否为最后一次修改
		private String	modifyLast;
		//修改id
		private String modifyId;
		//修改人工号
		private String modifyUserid;
		//修改时间
		private String modifyTime;
		//拨打类型
		private String optrType;
		//拨打时间
		private String dialTime;
		//呼叫流水号
		private String customerCallId;
		
		
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
			return iid;
		}
		public void setIid(String iid) {
			this.iid = iid;
		}
		public String getCid() {
			return cid;
		}
		public void setCid(String cid) {
			this.cid = cid;
		}
		public String getModifyLast() {
			return modifyLast;
		}
		public void setModifyLast(String modifyLast) {
			this.modifyLast = modifyLast;
		}
		public String getModifyId() {
			return modifyId;
		}
		public void setModifyId(String modifyId) {
			this.modifyId = modifyId;
		}
		public String getModifyUserid() {
			return modifyUserid;
		}
		public void setModifyUserid(String modifyUserid) {
			this.modifyUserid = modifyUserid;
		}
		public String getModifyTime() {
			return modifyTime;
		}
		public void setModifyTime(String modifyTime) {
			this.modifyTime = modifyTime;
		}
		public String getOptrType() {
			return optrType;
		}
		public void setOptrType(String optrType) {
			this.optrType = optrType;
		}
		public String getDialTime() {
			return dialTime;
		}
		public void setDialTime(String dialTime) {
			this.dialTime = dialTime;
		}
		public String getCustomerCallId() {
			return customerCallId;
		}
		public void setCustomerCallId(String customerCallId) {
			this.customerCallId = customerCallId;
		}
}
