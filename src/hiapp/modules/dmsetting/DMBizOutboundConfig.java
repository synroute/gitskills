package hiapp.modules.dmsetting;
/** 
 * @author yangwentian 
 * @version 创建时间：2017年9月8日 下午3:54:41 
 * 类说明  外呼策略
 */
public class DMBizOutboundConfig {
	private int id;						//外呼策略ID
	private int bizId;					//业务ID
	private String outboundConfigJson;	//外呼配置信息json
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getBizId() {
		return bizId;
	}
	public void setBizId(int bizId) {
		this.bizId = bizId;
	}
	public String getOutboundConfigJson() {
		return outboundConfigJson;
	}
	public void setOutboundConfigJson(String outboundConfigJson) {
		this.outboundConfigJson = outboundConfigJson;
	}
	
	
}
